package com.cs4103.server;

import com.cs4103.client.service.PaxosService;
import com.cs4103.shared.message.AcceptedMessage;
import com.cs4103.shared.message.AckMessage;
import com.cs4103.shared.message.InvitationMessage;
import com.cs4103.shared.message.ProposalMessage;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class PaxosServiceImpl extends RemoteServiceServlet implements
        PaxosService {
    private Logger logger = Logger.getLogger("Logger");
    private Map<Integer, List<InvitationMessage>> invitationMessagePool = Collections.synchronizedMap(new HashMap<>());
    private Map<Integer, List<AckMessage>> ackMessagePool = Collections.synchronizedMap(new HashMap<>());
    private Map<Integer, List<ProposalMessage>> proposalMessagePool = Collections.synchronizedMap(new HashMap<>()); // Quorum
    private Map<Integer, List<AcceptedMessage>> acceptedMessagePool = Collections.synchronizedMap(new HashMap<>());

    private List<Integer> availableClients = Collections.synchronizedList(new ArrayList<>());
    private Integer uniqueProcessId = 0;

    // For simulation of network delay
    private Random random = new Random();

    // --------------------------- Common activity ---------------------------
    @Override
    public Integer register() {
//        simulateNetworkDelay();
        synchronized (uniqueProcessId) {
            ++uniqueProcessId;
            // initialize pools
            invitationMessagePool.put(uniqueProcessId, new ArrayList<>());
            ackMessagePool.put(uniqueProcessId, new ArrayList<>());
            acceptedMessagePool.put(uniqueProcessId, new ArrayList<>());
            proposalMessagePool.put(uniqueProcessId, new ArrayList<>());
            availableClients.add(uniqueProcessId);
            return uniqueProcessId;
        }
    }

    @Override
    public Integer[] getAvailableClients() {
//        simulateNetworkDelay();
        synchronized (availableClients) {
            return availableClients.toArray(new Integer[availableClients.size()]);
        }
    }

    // --------------------------- Proposer Activity ---------------------------

    @Override
    public void prepare(List<InvitationMessage> invitationMessages) {
//        simulateNetworkDelay();
        // Only one agent should not send invitation
        if (uniqueProcessId == 1) {
            return;
        }

        int fromId;
        if (invitationMessages == null) {
            return;
        } else {
            fromId = invitationMessages.get(0).getFromId();
        }

        // put the invitation to the client pool, and later the client poll their invitation.
        synchronized (invitationMessagePool) {
            for (InvitationMessage invitationMessage : invitationMessages) {
                if (invitationMessagePool.get(invitationMessage.getToId()) == null) {
                    return;
                }
                invitationMessagePool.get(invitationMessage.getToId()).add(invitationMessage);
            }
        }
    }

    @Override
    public AckMessage pollAckMessage(int clientId) {
//        simulateNetworkDelay();
        synchronized (ackMessagePool) {
            if (ackMessagePool.get(clientId).size() == 0) {
                return null;
            }
            return ackMessagePool.get(clientId).remove(0);
        }
    }

    @Override
    public void proposeValue(List<ProposalMessage> proposalMessages) {
//        simulateNetworkDelay();
        synchronized (proposalMessagePool) {
            // add each proposal to each agents' pool
            for (ProposalMessage proposalMessage :
                    proposalMessages) {
                proposalMessagePool.get(proposalMessage.getToId()).add(proposalMessage);
            }
        }
    }

    // --------------------------- Acceptor Activity ---------------------------
    @Override
    public InvitationMessage pollInvitation(int clientId) {
//        simulateNetworkDelay();
        synchronized (invitationMessagePool) {
            if (invitationMessagePool.get(clientId) == null || invitationMessagePool.get(clientId).size() == 0) {
                return null;
            }

            // poll the first prepare message
            return invitationMessagePool.get(clientId).remove(0);
        }
    }

    @Override
    public void sendAckMessage(AckMessage ackMessage) {
//        simulateNetworkDelay();
        synchronized (ackMessagePool) {
            ackMessagePool.get(ackMessage.getToId()).add(ackMessage);
        }
    }

    @Override
    public ProposalMessage pollProposalMessage(int clientId) {
//        simulateNetworkDelay();
        synchronized (proposalMessagePool) {
            if (proposalMessagePool.get(clientId) == null || proposalMessagePool.get(clientId).size() == 0) {
                return null;
            }
            return proposalMessagePool.get(clientId).remove(0);
        }
    }

    @Override
    public void sendAcceptedMessage(AcceptedMessage acceptedMessage) {
//        simulateNetworkDelay();
        Integer[] availableClientIds = getAvailableClients();

        synchronized (acceptedMessagePool) {
            for (Integer clientId : availableClientIds) {
                acceptedMessagePool.get(clientId).add(acceptedMessage);
            }
        }
    }

    // --------------------------- Learner Activity ---------------------------

    @Override
    public AcceptedMessage pollAllAcceptors(int clientId) {
//        simulateNetworkDelay();
        synchronized (acceptedMessagePool) {
            // if the learner does not have any acceptedMessage, return null.
            if (acceptedMessagePool.get(clientId) == null || acceptedMessagePool.get(clientId).size() == 0) {
                return null;
            }
            return acceptedMessagePool.get(clientId).remove(0);
        }
    }

    // --------------------------- Network delay simulation ---------------------------

    private void simulateNetworkDelay() {
        try {
            Thread.sleep(random.nextInt(3) + 1); // between 1s and 3s
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
