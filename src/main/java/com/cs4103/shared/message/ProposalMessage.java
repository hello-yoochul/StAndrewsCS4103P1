package com.cs4103.shared.message;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProposalMessage implements IsSerializable {
    private int fromId;
    private int toId;
    private int ballotId;
    private String decreeValue;

    public ProposalMessage() {
    }

    public ProposalMessage(int fromId, int toId, int ballotId, String decreeValue) {
        this.fromId = fromId;
        this.toId = toId;
        this.ballotId = ballotId;
        this.decreeValue = decreeValue;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public void setBallotId(int ballotId) {
        this.ballotId = ballotId;
    }

    public void setDecreeValue(String decreeValue) {
        this.decreeValue = decreeValue;
    }

    public int getFromId() {
        return fromId;
    }

    public int getToId() {
        return toId;
    }

    public String getDecreeValue() {
        return decreeValue;
    }

    public int getBallotId() {
        return ballotId;
    }
}
