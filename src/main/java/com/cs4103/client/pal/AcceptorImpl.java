package com.cs4103.client.pal;

import com.cs4103.client.service.PaxosServiceAsync;
import com.cs4103.shared.message.AcceptedMessage;
import com.cs4103.shared.message.AckMessage;
import com.cs4103.shared.message.InvitationMessage;
import com.cs4103.shared.message.ProposalMessage;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.cs4103.shared.message.AckMessage.*;

public class AcceptorImpl implements Acceptor {
    private Logger logger = Logger.getLogger("Logger");
    private final static int MESSAGE_REFRESH_INTERVAL = 1000;
    private PaxosServiceAsync paxosService;
    private ProposerImpl proposer;

    private int clientId;
    private int minIdToAccept;
    private String lastAcceptedValue;
    private int lastAcceptedId;

    private boolean isPromiseActive = false;

    private AckMessage ackMessage;

    public AcceptorImpl() {
    }

    public AcceptorImpl(PaxosServiceAsync paxosService, int clientId, ProposerImpl proposer) {
        this.paxosService = paxosService;
        this.clientId = clientId;
        this.proposer = proposer;

        minIdToAccept = 0;
        lastAcceptedId = 0;
        lastAcceptedValue = null;

        beReadyForPrepareMessage();
        beReadyForProposalMessage();
    }

    private void beReadyForPrepareMessage() {
        // regularly polls for a prepare message
        Timer prepareMessageRefreshTimer = new Timer() {
            @Override
            public void run() {
                receivePrepare();
            }
        };
        prepareMessageRefreshTimer.scheduleRepeating(MESSAGE_REFRESH_INTERVAL); // 1s
    }

    private void beReadyForProposalMessage() {
        // regularly polls for a proposal message
        Timer prepareMessageRefreshTimer = new Timer() {
            @Override
            public void run() {
                pollProposalMessage();
            }
        };
        prepareMessageRefreshTimer.scheduleRepeating(MESSAGE_REFRESH_INTERVAL); // 1s
    }

    /**
     * Receive a prepare message (= invitation).
     * If the id is lower than local minIdToAccept, make a negative ack message,
     * otherwise make a positive ack message. An Acceptor can accept multiple
     * proposals, even though it has accepted another one earlier.
     */
    @Override
    public void receivePrepare() {
        paxosService.pollInvitation(clientId, new AsyncCallback<InvitationMessage>() {
            @Override
            public void onFailure(Throwable caught) {
//                Window.alert("error occurred while receiving a prepare message");
                logger.log(Level.INFO, "error in receivePrepare()");
                logger.log(Level.INFO, caught.getMessage());
            }

            @Override
            public void onSuccess(InvitationMessage invitationMessage) {
                if (invitationMessage == null) {
                    return;
                }

                ackMessage = new AckMessage();
                ackMessage.setToId(invitationMessage.getFromId());

                if (minIdToAccept < invitationMessage.getBallotId()) {
                    isPromiseActive = true;
                    minIdToAccept = invitationMessage.getBallotId();
                    ackMessage.setResponse(Response.POSITIVE);
                    // if it has received id and value previously, add it to the ack message.
                    if (lastAcceptedId != 0 || lastAcceptedValue != null) {
                        ackMessage.setBallotId(lastAcceptedId);
                        ackMessage.setValue(lastAcceptedValue);
                    }
                    ackMessage.setFromId(clientId);
                } else {
                    ackMessage.setResponse(Response.NEGATIVE);
                    ackMessage.setFromId(clientId);
                }
                sendAckMessage();
            }
        });
    }

    /**
     * Send a response message (promise phase).
     */
    @Override
    public void sendAckMessage() {
        paxosService.sendAckMessage(ackMessage, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
//                Window.alert("error occurred while sending an ack message");
                logger.log(Level.INFO, "error in sendAckMessage()");
                logger.log(Level.INFO, caught.getMessage());
            }

            @Override
            public void onSuccess(Void unused) {
            }
        });
    }

    /**
     * Poll the proposal message from its own pool in the server.
     */
    @Override
    public void pollProposalMessage() {
        paxosService.pollProposalMessage(clientId, new AsyncCallback<ProposalMessage>() {
            @Override
            public void onFailure(Throwable caught) {
//                Window.alert("error occurred while getting the proposal message");
                logger.log(Level.INFO, "error in pollProposalMessage()");
                logger.log(Level.INFO, caught.getMessage());
            }

            @Override
            public void onSuccess(ProposalMessage proposalMessage) {
                if (proposalMessage == null) {
                    return;
                }

                if (isPromiseActive) {
                    if (proposalMessage.getBallotId() == minIdToAccept) {
                        lastAcceptedValue = proposalMessage.getDecreeValue();
                        lastAcceptedId = proposalMessage.getBallotId();
                        isPromiseActive = false;
                        proposer.setBallotId(minIdToAccept);
                        sendToLearners();
                    }
                }
            }
        });
    }

    /**
     * Send the accepted message to all learners.
     */
    private void sendToLearners() {
        AcceptedMessage acceptedMessage = new AcceptedMessage(lastAcceptedValue, lastAcceptedId);
        paxosService.sendAcceptedMessage(acceptedMessage, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
//                Window.alert("error occurred while sending the accepted message to learners");
                logger.log(Level.INFO, "error in pollProposalMessage()");
                logger.log(Level.INFO, caught.getMessage());
            }

            @Override
            public void onSuccess(Void unused) {
            }
        });
    }
}
