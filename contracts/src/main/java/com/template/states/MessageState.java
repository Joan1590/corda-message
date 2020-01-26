package com.template.states;

import com.template.contracts.MessageContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(MessageContract.class)
public class MessageState implements ContractState {

    private final String message;
    private final Party lender;
    private final Party borrower;

    public MessageState(String message, Party lender, Party borrower) {
        this.message = message;
        this.lender = lender;
        this.borrower = borrower;
    }

    public String getMessage() {
        return message;
    }

    public Party getLender() {
        return lender;
    }

    public Party getBorrower() {
        return borrower;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(lender, borrower);
    }
}