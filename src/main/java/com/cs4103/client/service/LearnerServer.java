package com.cs4103.client.service;

import com.cs4103.shared.message.AcceptedMessage;

public interface LearnerServer {
    /**
     * Poll the accepted message in the pool.
     *
     * @param clientId the learner position in the pool.
     * @return the accepted message in the pool.
     */
    AcceptedMessage pollAllAcceptors(int clientId);
}
