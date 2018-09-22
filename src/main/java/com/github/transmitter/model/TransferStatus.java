package com.github.transmitter.model;

import lombok.Value;

import javax.ws.rs.core.Response;

@Value(staticConstructor = "from")
public class TransferStatus {
    Response.Status status;
    String messageMask;
}
