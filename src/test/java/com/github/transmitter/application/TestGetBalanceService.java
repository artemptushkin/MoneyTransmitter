package com.github.transmitter.application;

import com.github.transmitter.model.Account;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.math.BigDecimal;

@Path("/get")
public class TestGetBalanceService {
    @PersistenceContext
    private EntityManager entityManager;

    @GET
    public BigDecimal get(@QueryParam(value = "id") Long id) {
        return entityManager.find(Account.class, id)
                .getBalance();
    }
}
