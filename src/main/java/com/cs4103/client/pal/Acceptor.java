package com.cs4103.client.pal;

public interface Acceptor {
    /**
     * Receive a prepare message (= invitation).
     * If the id is lower than local minIdToAccept, make a negative ack message,
     * otherwise make a positive ack message. An Acceptor can accept multiple
     * proposals, even though it has accepted another one earlier.
     */
    void receivePrepare();

    /**
     * Send a response message (promise phase).
     */
    void sendAckMessage();

    /**
     * Poll the proposal message from its own pool in the server.
     */
    void pollProposalMessage();
}
