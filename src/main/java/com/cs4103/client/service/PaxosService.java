package com.cs4103.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

@RemoteServiceRelativePath("greet")
public interface PaxosService extends RemoteService, ProposerServer, AcceptorServer, LearnerServer {
    /**
     * Register and get the unique id.
     *
     * @return the unique id.
     */
    Integer register();

    /**
     * Get available client IDs.
     *
     * @return available client ids.
     */
    Integer[] getAvailableClients();
}
