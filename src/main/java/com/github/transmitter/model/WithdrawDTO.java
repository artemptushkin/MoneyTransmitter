package com.github.transmitter.model;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class WithdrawDTO implements TransferEntity {
    Long id;
    BigDecimal amount;
}
