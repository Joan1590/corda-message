package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.states.MessageState;
import net.corda.core.contracts.ContractState;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

import static net.corda.core.contracts.ContractsDSL.requireThat;

// ******************
// * Responder flow *
// ******************
// Replace Responder's definition with:
@InitiatedBy(MessageFlow.class)
public class MessageFlowResponder extends FlowLogic<SignedTransaction> {
    private final FlowSession otherPartySession;

    public MessageFlowResponder(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        class SignTxFlow extends SignTransactionFlow {
            private SignTxFlow(FlowSession otherPartySession) {
                super(otherPartySession);
            }

            @Override
            protected void checkTransaction(SignedTransaction stx) {
                requireThat(require -> {
                    ContractState output = stx.getTx().getOutputs().get(0).getData();
                    require.using("This must be an transaction.", output instanceof MessageState);
                    MessageState message = (MessageState) output;
                    require.using("The Message's value can't be too high.", message.getMessage().length() < 255);
                    return null;
                });
            }
        }

        SecureHash expectedTxId = subFlow(new SignTxFlow(otherPartySession)).getId();

        return subFlow(new ReceiveFinalityFlow(otherPartySession, expectedTxId));

    }
}