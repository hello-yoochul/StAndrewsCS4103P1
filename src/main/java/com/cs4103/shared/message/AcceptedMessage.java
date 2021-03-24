package com.cs4103.shared.message;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AcceptedMessage implements IsSerializable {
    private String lastAcceptedValue;
    private int lastAcceptedId;

    public AcceptedMessage() {
    }

    public AcceptedMessage(String lastAcceptedValue, int lastAcceptedId) {
        this.lastAcceptedValue = lastAcceptedValue;
        this.lastAcceptedId = lastAcceptedId;
    }

    public void setLastAcceptedValue(String lastAcceptedValue) {
        this.lastAcceptedValue = lastAcceptedValue;
    }

    public void setLastAcceptedId(int lastAcceptedId) {
        this.lastAcceptedId = lastAcceptedId;
    }

    public String getLastAcceptedValue() {
        return lastAcceptedValue;
    }

    public int getLastAcceptedId() {
        return lastAcceptedId;
    }
}
