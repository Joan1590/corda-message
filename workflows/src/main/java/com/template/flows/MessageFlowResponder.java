package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;

// ******************
// * Responder flow *
// ******************
// Replace Responder's definition with:
@InitiatedBy(MessageFlow.class)
public class MessageFlowResponder extends FlowLogic<Void> {
    private final FlowSession otherPartySession;

    public MessageFlowResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        subFlow(new ReceiveFinalityFlow(otherPartySession));

        return null;
    }
}