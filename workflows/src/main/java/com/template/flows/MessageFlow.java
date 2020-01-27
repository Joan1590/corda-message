package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.MessageContract;
import com.template.states.MessageState;
import net.corda.core.flows.*;
import net.corda.core.utilities.ProgressTracker;

// Add these imports:
import net.corda.core.contracts.Command;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

// Replace Initiator's definition with:
@InitiatingFlow
@StartableByRPC
public class MessageFlow extends FlowLogic<SignedTransaction> {
    private final String message;
    private final Party otherParty;

    /**
     * The progress tracker provides checkpoints indicating the progress of the flow to observers.
     */
    private final ProgressTracker progressTracker = new ProgressTracker();

    public MessageFlow(String message, Party otherParty) {
        this.message = message;
        this.otherParty = otherParty;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    /**
     * The flow logic is encapsulated within the call() method.
     */
    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // We retrieve the notary identity from the network map.
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        // We create the transaction components.
        MessageState outputState = new MessageState(message, getOurIdentity(), otherParty);
        List<PublicKey> requiredSigners = Arrays.asList(getOurIdentity().getOwningKey(), otherParty.getOwningKey());
        Command command = new Command<>(new MessageContract.Create(), requiredSigners);

        // We create a transaction builder and add the components.
        TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addOutputState(outputState, MessageContract.ID)
                .addCommand(command);

        // Verifying the transaction.
        txBuilder.verify(getServiceHub());

        // Signing the transaction.
        SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

        // Creating a session with the other party.
        FlowSession otherPartySession = initiateFlow(otherParty);

        // Obtaining the counterparty's signature.
        SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(
                signedTx, Arrays.asList(otherPartySession), CollectSignaturesFlow.tracker()));

        // Finalising the transaction.
        return subFlow(new FinalityFlow(fullySignedTx, otherPartySession));
    }
}