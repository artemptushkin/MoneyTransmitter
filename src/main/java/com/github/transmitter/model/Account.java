package com.github.transmitter.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Account entity
 * {@link #balance} can be less or more then 0
 * default balance value is 0 and is changing after deserialization
 * DECIMAL(19, 4) - as standard money precision and scale
 */
@Data
@Entity
@Table(name = "account")
public class Account {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column(name = "balance", precision = 19, scale = 4, columnDefinition="Decimal(19,4) default '0.0000'")
    BigDecimal balance = BigDecimal.ZERO;

    public void deposit(BigDecimal amount) {
        this.balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        this.balance = balance.subtract(amount);
    }
}
