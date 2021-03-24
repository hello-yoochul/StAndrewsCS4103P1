package com.cs4103.client.pal;

import com.cs4103.client.view.PaxosEntryView;

public interface Learner {
    /**
     * Poll regularly all the accepted messages to get <lastAcceptedValue, lastAcceptedId> pair.
     * The decree value will be set on the {@link PaxosEntryView#receiveMessageField}
     * (If a learner gets the same result from a majority, that is a successful ballot. i.e., the consensus value.)
     */
    void pollAllAcceptors();
}
