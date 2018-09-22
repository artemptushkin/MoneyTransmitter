package com.github.transmitter.service;

import com.github.transmitter.exception.AccountLackException;
import com.github.transmitter.exception.AccountNotFoundException;
import com.github.transmitter.model.Account;
import com.github.transmitter.model.DepositDTO;
import com.github.transmitter.model.TransferEntity;
import com.github.transmitter.model.WithdrawDTO;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;

@Named
@RequestScoped
public class AccountService {
    @PersistenceContext(name = "transmitter-jpa")
    private EntityManager em;

    @Transactional(rollbackOn = {AccountNotFoundException.class, AccountLackException.class})
    public void withdraw(WithdrawDTO withdrawDTO) throws AccountNotFoundException, AccountLackException {
        Account targetAccount = getTargetAccount(withdrawDTO.getId());

        assertAccountExists(targetAccount, withdrawDTO);

        BigDecimal withdrawAmount = withdrawDTO.getAmount();
        assertAccountLack(targetAccount, withdrawAmount);

        targetAccount.withdraw(withdrawAmount);
    }

    @Transactional(rollbackOn = AccountNotFoundException.class)
    public void deposit(DepositDTO depositDTO) throws AccountNotFoundException {
        Account targetAccount = getTargetAccount(depositDTO.getId());

        assertAccountExists(targetAccount, depositDTO);

        targetAccount.deposit(depositDTO.getAmount());
    }

    private void assertAccountLack(Account account, BigDecimal withdrawAmount) throws AccountLackException {
        if (account.getBalance().compareTo(withdrawAmount) < 0) {
            throw new AccountLackException(account.getId(), withdrawAmount);
        }
    }

    private void assertAccountExists(Account account, TransferEntity transferEntity) throws AccountNotFoundException {
        if (account == null) {
            throw new AccountNotFoundException(transferEntity.getId());
        }
    }

    private Account getTargetAccount(Long id) {
        return em.find(Account.class, id);
    }
}
