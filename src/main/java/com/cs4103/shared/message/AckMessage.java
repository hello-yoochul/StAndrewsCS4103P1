package com.cs4103.shared.message;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AckMessage implements IsSerializable, Comparable<AckMessage> {
    public enum Response implements IsSerializable {
        POSITIVE, NEGATIVE;

        Response() {
        }
    }

    private int fromId;
    private int toId;
    private int ballotId;
    private String value;
    private Response response;

    public AckMessage() {
    }

    public AckMessage(int ballotId, String value) {
        this.ballotId = ballotId;
        this.value = value;
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

    public void setValue(String value) {
        this.value = value;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public int getFromId() {
        return fromId;
    }

    public int getToId() {
        return toId;
    }

    public int getBallotId() {
        return ballotId;
    }

    public String getValue() {
        return value;
    }

    public Response getResponse() {
        return response;
    }

    @Override
    public int compareTo(AckMessage ackMessage) {
        return this.ballotId - ackMessage.getBallotId();
    }
}
