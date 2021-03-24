package com.cs4103.shared.message;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PollMessage implements IsSerializable {
    private int lastAcceptedValue;
    private int lastAcceptedId;

    public PollMessage() {
    }

    public PollMessage(int lastAcceptedValue, int lastAcceptedId) {
        this.lastAcceptedValue = lastAcceptedValue;
        this.lastAcceptedId = lastAcceptedId;
    }

    public void setLastAcceptedValue(int lastAcceptedValue) {
        this.lastAcceptedValue = lastAcceptedValue;
    }

    public void setLastAcceptedId(int lastAcceptedId) {
        this.lastAcceptedId = lastAcceptedId;
    }

    public int getLastAcceptedValue() {
        return lastAcceptedValue;
    }

    public int getLastAcceptedId() {
        return lastAcceptedId;
    }
}
