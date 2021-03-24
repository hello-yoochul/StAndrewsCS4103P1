package com.cs4103.shared.message;

import com.google.gwt.user.client.rpc.IsSerializable;

public class InvitationMessage implements IsSerializable {
    private int fromId;
    private int toId;
    private int ballotId;

    public InvitationMessage() {
    }

    public InvitationMessage(int fromId, int toId, int ballotId) {
        this.fromId = fromId;
        this.toId = toId;
        this.ballotId = ballotId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public void setBallotId(int ballotId) {
        this.ballotId = ballotId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public int getFromId() {
        return fromId;
    }

    public int getBallotId() {
        return ballotId;
    }

    public int getToId() {
        return toId;
    }
}
