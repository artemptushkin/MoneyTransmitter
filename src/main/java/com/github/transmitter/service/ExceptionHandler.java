package com.github.transmitter.service;

import com.github.transmitter.exception.ExceptionStorage;
import com.github.transmitter.model.TransferResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@ApplicationScoped
public class ExceptionHandler {
    @Inject
    private ExceptionStorage exceptionStatusMap;

    public TransferResponse handle(Exception exception) {
        Response.Status status = exceptionStatusMap.get(exception);
        if (status == null) {
            status = INTERNAL_SERVER_ERROR;
        }
        return TransferResponse.from(status, exception.getMessage());
    }
}
