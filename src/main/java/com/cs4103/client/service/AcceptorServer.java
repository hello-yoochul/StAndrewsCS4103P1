package com.cs4103.client.service;

import com.cs4103.shared.message.AcceptedMessage;
import com.cs4103.shared.message.AckMessage;
import com.cs4103.shared.message.InvitationMessage;
import com.cs4103.shared.message.ProposalMessage;

public interface AcceptorServer {
    /**
     * Poll the invitation message from other agents.
     *
     * @param clientId the invitation pool position.
     * @return the first among invitationMessage.
     */
    InvitationMessage pollInvitation(int clientId);

    /**
     * AckMessages will be stored in the each agents' pool.
     * And the proposer will poll the messages to check if the majority answered positively.
     *
     * @param ackMessage response to prepare message.
     */
    void sendAckMessage(AckMessage ackMessage);

    /**
     * Poll the proposal message from proposers.
     * Acceptor would get the proposal message, if they have agreed to join the quorum.
     *
     * @param clientId the proposal pool position.
     * @return the proposal message.
     */
    ProposalMessage pollProposalMessage(int clientId);

    /**
     * Send the accepted message to all learners.
     * Each learner (accepted message) pool will hold the accepted message, and they will poll it.
     *
     * @param acceptedMessage accepted message.
     */
    void sendAcceptedMessage(AcceptedMessage acceptedMessage);
}
