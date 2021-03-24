package com.cs4103.client.pal;

import com.cs4103.client.service.PaxosServiceAsync;
import com.cs4103.client.view.PaxosEntryView;
import com.cs4103.shared.message.AcceptedMessage;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LearnerImpl implements Learner {
    private final static int MESSAGE_REFRESH_INTERVAL = 1000;
    private Logger logger = Logger.getLogger("Logger");
    private PaxosEntryView paxosEntryView;
    private PaxosServiceAsync paxosService;

    private int clientId;
    private String lastAcceptedValue;
    private int lastAcceptedId;

    public LearnerImpl() {
    }

    public LearnerImpl(PaxosEntryView paxosEntryView, PaxosServiceAsync paxosService, int clientId) {
        this.paxosEntryView = paxosEntryView;
        this.paxosService = paxosService;
        this.clientId = clientId;

        beReadyForAcceptedMessage();
    }

    private void beReadyForAcceptedMessage() {
        Timer acceptedMessageRefreshTimer = new Timer() {
            @Override
            public void run() {
                pollAllAcceptors();
            }
        };
        acceptedMessageRefreshTimer.scheduleRepeating(MESSAGE_REFRESH_INTERVAL); // 1s
    }

    /**
     * Poll regularly all the accepted messages to get <lastAcceptedValue, lastAcceptedId> pair.
     * The decree value will be set on the {@link PaxosEntryView#receiveMessageField}
     * (If a learner gets the same result from a majority, that is a successful ballot. i.e., the consensus value.)
     */
    @Override
    public void pollAllAcceptors() {
        paxosService.pollAllAcceptors(clientId, new AsyncCallback<AcceptedMessage>() {
            @Override
            public void onFailure(Throwable caught) {
//                Window.alert("error occurred while polling an accepted message");
                logger.log(Level.INFO, "error in pollAllAcceptors()");
                logger.log(Level.INFO, caught.getMessage());
            }

            @Override
            public void onSuccess(AcceptedMessage acceptedMessage) { //<lastAcceptedValue, lastAcceptedId> pair.
                if (acceptedMessage != null) {
                    lastAcceptedId = acceptedMessage.getLastAcceptedId();
                    lastAcceptedValue = acceptedMessage.getLastAcceptedValue();

                    // set the text in the view
                    paxosEntryView.setReceiveMessageField("lastAcceptedId: " + lastAcceptedId + ", lastAcceptedValue:" + lastAcceptedValue);
                }
            }
        });
    }
}
