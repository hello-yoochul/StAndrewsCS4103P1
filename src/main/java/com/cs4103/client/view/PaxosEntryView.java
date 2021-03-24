package com.cs4103.client.view;

import com.cs4103.client.pal.*;
import com.cs4103.client.service.PaxosService;
import com.cs4103.client.service.PaxosServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PaxosEntryView implements EntryPoint {
    private Logger logger = Logger.getLogger("Logger");
    private final PaxosServiceAsync paxosService = GWT.create(PaxosService.class);

    private int clientId;
    private ProposerImpl proposer;
    private AcceptorImpl acceptor;
    private LearnerImpl learner;

    private final Label uniqueClientIdLabel = new Label();
    private final TextArea messageInputField = new TextArea();
    private final Button sendButton = new Button("Send");
    private final Label availableClientIdLabel = new Label();
    private final TextArea receiveMessageField = new TextArea();

    public void onModuleLoad() {
        setUp();
    }

    /**
     * Set up the UI and register the agent (browser) to the server.
     */
    private void setUp() {
        messageInputField.setCharacterWidth(50);
        messageInputField.setVisibleLines(3);
        sendButton.addStyleName("my-button");
        receiveMessageField.setCharacterWidth(50);
        receiveMessageField.setVisibleLines(10);
        receiveMessageField.setReadOnly(true);

        RootPanel.get("uniqueClientId").add(uniqueClientIdLabel);
        RootPanel.get("messageTextArea").add(messageInputField);
        RootPanel.get("sendButton").add(sendButton);
        RootPanel.get("receivedMessageTextArea").add(receiveMessageField);
        RootPanel.get("availableClientIds").add(availableClientIdLabel);

        // when the browser is newly opened, it gets a new id.
        paxosService.register(new AsyncCallback<Integer>() {
            @Override
            public void onFailure(Throwable caught) {
//                Window.alert("error occurred while registering a client");
                logger.log(Level.INFO, "error in register()");
                logger.log(Level.INFO, caught.getMessage());
            }

            @Override
            public void onSuccess(Integer uniqueClientId) {
                clientId = uniqueClientId;
                makePAL();
                uniqueClientIdLabel.setText(Integer.toString(uniqueClientId));
            }
        });

        // when one writes the text and the send button is clicked, the prepare method of proposer is executed.
        sendButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (availableClientIdLabel.getText().isEmpty()) {
                    Window.alert("Please wait til the client ids become gathered...");
                    return;
                }

                if(messageInputField.getText().isEmpty()){
                    Window.alert("Please write message...");
                    return;
                }
                proposer.prepare();
            }
        });
    }

    /**
     * Make proposer, acceptor, and learner.
     */
    private void makePAL() {
        proposer = new ProposerImpl(this, paxosService, clientId);
        // When an acceptor accepts a proposal, it will set the ballot it in its proposer, so
        // proposer reference is sent via a parameter.
        acceptor = new AcceptorImpl(paxosService, clientId, proposer);
        learner = new LearnerImpl(this, paxosService, clientId);
    }

    /**
     * Set the received message to the field.
     */
    public void setReceiveMessageField(String receivedMessage) {
        receiveMessageField.setText(receivedMessage);
    }

    /**
     * Set the available client ids to the field.
     */
    public void setAvailableClientId(List<Integer> availableClientIds) {
        String ids = "";
        for (Integer id : availableClientIds) {
            ids += id;
            ids += " ";
        }
        availableClientIdLabel.setText(ids);
    }

    public String getProposalMessage() {
        return messageInputField.getText();
    }
}
