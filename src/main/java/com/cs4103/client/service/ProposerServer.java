package com.cs4103.client.service;

import com.cs4103.shared.message.AckMessage;
import com.cs4103.shared.message.InvitationMessage;
import com.cs4103.shared.message.ProposalMessage;

import java.util.List;

public interface ProposerServer {
    /**
     * Send prepare messages to all acceptors.
     *
     * @param invitationMessage the prepare messages.
     */
    void prepare(List<InvitationMessage> invitationMessage);

    /**
     * Poll the ack (response) messages from the pool.
     *
     * @param clientId the the pool position.
     * @return the first ack message in the pool.
     */
    AckMessage pollAckMessage(int clientId);

    /**
     * Propose the value to the acceptors who responded positive ack.
     *
     * @param proposalMessages the proposal message.
     */
    void proposeValue(List<ProposalMessage> proposalMessages);
}
