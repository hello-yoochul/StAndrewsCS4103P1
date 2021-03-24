package com.cs4103.client.service;

import com.cs4103.shared.message.AcceptedMessage;
import com.cs4103.shared.message.AckMessage;
import com.cs4103.shared.message.InvitationMessage;
import com.cs4103.shared.message.ProposalMessage;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface PaxosServiceAsync {
    // ----------------------- Common Activity -----------------------
    void register(AsyncCallback<Integer> async);

    void getAvailableClients(AsyncCallback<Integer[]> async);

    // ----------------------- Proposer Activity -----------------------
    void prepare(List<InvitationMessage> invitationMessage, AsyncCallback<Void> async);

    void pollAckMessage(int clientId, AsyncCallback<AckMessage> async);

    void proposeValue(List<ProposalMessage> proposalMessage, AsyncCallback<Void> async);

    // ----------------------- Acceptor Activity -----------------------
    void pollInvitation(int clientId, AsyncCallback<InvitationMessage> async);

    void sendAckMessage(AckMessage ackMessage, AsyncCallback<Void> async);

    void pollProposalMessage(int clientId, AsyncCallback<ProposalMessage> async);

    void sendAcceptedMessage(AcceptedMessage acceptedMessage, AsyncCallback<Void> async);

    // ----------------------- Learner Activity -----------------------
    void pollAllAcceptors(int clientId,AsyncCallback<AcceptedMessage> async);
}
