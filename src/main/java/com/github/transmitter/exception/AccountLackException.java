package com.github.transmitter.exception;

import java.math.BigDecimal;

public class AccountLackException extends Exception {
    private static final String MESSAGE_MASK = "There is not money with amount: %s at the account id: %d";

    public AccountLackException(Long id, BigDecimal value) {
        super(String.format(MESSAGE_MASK, value, id));
    }
}
