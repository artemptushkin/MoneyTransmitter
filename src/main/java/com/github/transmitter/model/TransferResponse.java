package com.github.transmitter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.ws.rs.core.Response;

@Value(staticConstructor = "from")
public class TransferResponse {
    private static final Response.Status SUCCESS_STATUS = Response.Status.OK;

    @JsonIgnore
    Response.Status status;
    String message;

    public static TransferResponse success(String message) {
        return from(SUCCESS_STATUS, message);
    }

    @JsonProperty
    public int code() {
        return status.getStatusCode();
    }
}
