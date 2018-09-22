package com.github.transmitter.exception;

public class AccountNotFoundException extends Exception {
    private static final String MESSAGE_MASK = "Account with id: '%d' has not been found";

    public AccountNotFoundException(Long id) {
        super(String.format(MESSAGE_MASK, id));
    }
}
