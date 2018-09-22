package com.github.transmitter.exception;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@ApplicationScoped
public class ExceptionStorage {

    private Map<Class<? extends Exception>, Response.Status> exceptionStatusMap = new HashMap<>();

    @PostConstruct
    public void setUpStorage() {
        exceptionStatusMap.put(AccountNotFoundException.class, NOT_FOUND);
        exceptionStatusMap.put(AccountLackException.class, FORBIDDEN);
        exceptionStatusMap.put(NotAllowedTransferException.class, FORBIDDEN);
    }

    public Response.Status get(Exception exception) {
        return exceptionStatusMap.get(exception.getClass());
    }
}
