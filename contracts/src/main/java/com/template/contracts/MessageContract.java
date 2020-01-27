package com.template.contracts;

import com.template.states.MessageState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

import net.corda.core.contracts.CommandWithParties;
import net.corda.core.identity.Party;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

// Replace TemplateContract's definition with:
public class MessageContract implements Contract {
    public static final String ID = "com.template.contracts.MessageContract";

    // Our Create command.
    public static class Create implements CommandData {

    }

    @Override
    public void verify(LedgerTransaction tx) {
        final CommandWithParties<MessageContract.Create> command = requireSingleCommand(tx.getCommands(), MessageContract.Create.class);

        // Constraints on the shape of the transaction.
        if (!tx.getInputs().isEmpty())
            throw new IllegalArgumentException("No inputs should be consumed when issuing an IOU.");
        if (!(tx.getOutputs().size() == 1))
            throw new IllegalArgumentException("There should be one output state of type IOUState.");

        // IOU-specific constraints.
        final MessageState output = tx.outputsOfType(MessageState.class).get(0);
        final Party lender = output.getLender();
        final Party borrower = output.getBorrower();
        if (output.getMessage().length() <= 0)
            throw new IllegalArgumentException("You must to send a text.");
        if (lender.equals(borrower))
            throw new IllegalArgumentException("The lender and the borrower cannot be the same entity.");

        // Constraints on the signers.
        final List<PublicKey> requiredSigners = command.getSigners();
        final List<PublicKey> expectedSigners = Arrays.asList(borrower.getOwningKey(), lender.getOwningKey());
        if (requiredSigners.size() != 2)
            throw new IllegalArgumentException("There must be two signers.");
        if (!(requiredSigners.containsAll(expectedSigners)))
            throw new IllegalArgumentException("The borrower and lender must be signers.");

    }
}