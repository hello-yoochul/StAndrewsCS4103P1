package com.cs4103.client.pal;

import com.cs4103.client.service.PaxosServiceAsync;
import com.cs4103.client.view.PaxosEntryView;
import com.cs4103.shared.message.AckMessage;
import com.cs4103.shared.message.InvitationMessage;
import com.cs4103.shared.message.ProposalMessage;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProposerImpl implements Proposer {
    private Logger logger = Logger.getLogger("Logger");
    private final static int MESSAGE_REFRESH_INTERVAL = 1000;
    private PaxosEntryView paxosEntryView;
    private PaxosServiceAsync paxosService;

    private List<Integer> availableClientIds;

    private int clientId;
    private int ballotId;
    private String decreeValue;

    private List<InvitationMessage> invitationMessageList;
    private List<AckMessage> ackMessageList;
    private List<ProposalMessage> proposalMessageList;

    public ProposerImpl() {
    }

    public ProposerImpl(PaxosEntryView paxosEntryView, PaxosServiceAsync paxosService, int clientId) {
        this.paxosEntryView = paxosEntryView;
        this.paxosService = paxosService;
        this.clientId = clientId;

        availableClientIds = new ArrayList<>();
        ballotId = 0;

        getRegularlyAvailableClientIds();
    }

    private void getRegularlyAvailableClientIds() {
        // the client number is shared
        Timer availableClientRefreshTimer = new Timer() {
            @Override
            public void run() {
                paxosService.getAvailableClients(new AsyncCallback<Integer[]>() {
                    @Override
                    public void onFailure(Throwable caught) {
//                        Window.alert("error occurred while getting the client ids");
                        logger.log(Level.INFO, "error in getRegularlyAvailableClientIds()");
                        logger.log(Level.INFO, caught.getMessage());
                    }

                    @Override
                    public void onSuccess(Integer[] availableClientId) {
                        availableClientIds = Arrays.asList(availableClientId);
                        paxosEntryView.setAvailableClientId(availableClientIds);
                    }
                });
            }
        };
        availableClientRefreshTimer.scheduleRepeating(MESSAGE_REFRESH_INTERVAL); // 1s
    }

    /**
     * Send the prepare messages to all acceptors (invitation activity) and
     * wait until the all acceptors respond with positive or negative ack messages.
     * If the acceptor offers a decree value, this proposer will store the value.
     * If the number of acceptors, who responded positive ack message, is majority,
     * it will start to propose its value to the acceptors, otherwise stop the process.
     */
    @Override
    public void prepare() {
        // The ballot id must be greater than any number used in any of the previous Prepare messages.
        increaseId();

        if (availableClientIds.size() == 0) {
            return;
        }

        // make invitation message for the available agents
        makeInvitationMessages();

        // put the invitation message in the server pool and get the response, AckMessages.
        // if the acceptor has higher ballot id negative ack message will be obtained.
        paxosService.prepare(invitationMessageList, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
//                Window.alert("error occurred while sending the prepare message");
                logger.log(Level.INFO, "error in prepare()");
                logger.log(Level.INFO, caught.getMessage());
            }

            @Override
            public void onSuccess(Void unused) {
                // each invitation for the all acceptors should have one ack message pool, so initialize
                ackMessageList = new ArrayList<>();

                // regularly polls for ack message until the majority responses
                Timer ackMessageRefreshTimer = new Timer() {
                    @Override
                    public void run() {
                        paxosService.pollAckMessage(clientId, new AsyncCallback<AckMessage>() {
                            @Override
                            public void onFailure(Throwable caught) {
//                                Window.alert("error occurred while polling ack messages");
                                logger.log(Level.INFO, "ackMessageRefreshTimer error in prepare()");
                                logger.log(Level.INFO, caught.getMessage());
                            }

                            @Override
                            public void onSuccess(AckMessage ackMessage) {
                                // ack message pool in the server can be empty until the acceptors respond
                                // if so, return; to keep doing the refresh timer
                                if (ackMessage == null) {
                                    return;
                                }

                                ackMessageList.add(ackMessage);

                                // wait until all acceptors respond.
                                if (ackMessageList.size() != availableClientIds.size() - 1) { // remove itself
                                    return;
                                }

                                int positiveAckNumber = 0;

                                // count the positive ack message
                                for (AckMessage am : ackMessageList) {
                                    if (am.getResponse() == AckMessage.Response.POSITIVE) {
                                        ++positiveAckNumber;
                                    }
                                }

                                AckMessage mayHighestValueMessage = findHighestBallotIdAmongTheAckMessages();

                                // Set the decree value of the message with the highest ballot id in the ack message.
                                if (mayHighestValueMessage != null) {
                                    decreeValue = mayHighestValueMessage.getValue();
                                }

                                // send proposal message if majority responded positive.
                                // remove itself since this proposer is one who already agreed
                                if (positiveAckNumber >= (availableClientIds.size() - 1) / 2) {
                                    propose(); // the quorum is opened by calling this method
                                    cancel(); // stop this timer
                                } else {
                                    cancel(); // stop this timer
                                }
                            }
                        });
                    }
                };
                ackMessageRefreshTimer.scheduleRepeating(MESSAGE_REFRESH_INTERVAL); // 3s
            }
        });
    }

    private void makeInvitationMessages() {
        // Reset prepare messages every time when proposer send a prepare message.
        invitationMessageList = new ArrayList<>();

        // make invitation messages for the all agents
        for (Integer toId : availableClientIds) {
            // do not make invitation itself.
            if (toId != clientId) {
                InvitationMessage invitationMessage = new InvitationMessage(clientId, toId, ballotId);
                invitationMessageList.add(invitationMessage);
            }
        }
    }

    private AckMessage findHighestBallotIdAmongTheAckMessages() {
        // AckMessage implements Comparable which has compareTo method on ballotId, so it can be sorted.
        Collections.sort(ackMessageList, Comparator.reverseOrder());

        if (ackMessageList.get(0).getBallotId() > 0) {
            return ackMessageList.get(0); // the one with highest ballot id
        } else {
            return null;
        }
    }

    /**
     * Propose the decree value to the invited acceptors who responded positive ack message.
     */
    @Override
    public void propose() {
        // Selects the decree from the reply with the highest ballot id
        // if none, propose the decree here to the quorum, or its own intention
        List<ProposalMessage> proposalMessages = makeProposalMessages();

        paxosService.proposeValue(proposalMessages, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
//                Window.alert("error occurred while sending the propose message");
                logger.log(Level.INFO, "error in propose()");
                logger.log(Level.INFO, caught.getMessage());
            }

            @Override
            public void onSuccess(Void unused) {
            }
        });
    }

    private List<ProposalMessage> makeProposalMessages() {
        proposalMessageList = new ArrayList<>();

        // find the acceptors who agreed to join this quorum and make proposal messages.
        for (int i = 0; i < ackMessageList.size(); i++) {
            if (ackMessageList.get(i).getResponse() == AckMessage.Response.POSITIVE) {
                decreeValue = paxosEntryView.getProposalMessage();
                int toId = ackMessageList.get(i).getFromId();
                ProposalMessage proposalMessage = new ProposalMessage(clientId, toId, ballotId, decreeValue);
                proposalMessageList.add(proposalMessage);
            }
        }

        return proposalMessageList;
    }

    /**
     * Increase the ballot id.
     */
    @Override
    public void increaseId() {
        ++ballotId;
    }

    /**
     * When acceptors accept a proposal message, they will set the accepted ballot id in
     * its proposer here, as each node performs three roles. By doing so, the node of
     * acceptor can also do the propose activity with the ballot id.
     */
    @Override
    public void setBallotId(int ballotId) {
        this.ballotId = ballotId;
    }
}
