package com.cs4103.client.pal;

public interface Proposer {

    /**
     * Send the prepare messages to all acceptors (invitation activity) and
     * wait until the all acceptors respond with positive or negative ack messages.
     * If the acceptor offers a decree value, this proposer will store the value.
     * If the number of acceptors, who responded positive ack message, is majority,
     * it will start to propose its value to the acceptors, otherwise stop the process.
     */
    void prepare();

    /**
     * Propose the decree value to the invited acceptors who responded positive ack message.
     */
    void propose();

    /**
     * Increase the ballot id.
     */
    void increaseId();

    /**
     * When acceptors accept a proposal message, they will set the accepted ballot id in
     * its proposer here, as each node performs three roles. By doing so, the node of
     * acceptor can also do the propose activity with the ballot id.
     */
    void setBallotId(int ballotId);
}
