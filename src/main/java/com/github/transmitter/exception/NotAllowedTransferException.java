package com.github.transmitter.exception;

public class NotAllowedTransferException extends Exception {
    private static final String MESSAGE_MASK = "Transfer from id '%d' to id '%d' is not allowed";

    public NotAllowedTransferException(Long idFrom, Long idTo) {
        super(String.format(MESSAGE_MASK, idFrom, idTo));
    }

    public NotAllowedTransferException(String message) {
        super(message);
    }
}
