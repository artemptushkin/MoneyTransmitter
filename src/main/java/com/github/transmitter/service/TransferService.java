package com.github.transmitter.service;

import com.github.transmitter.exception.AccountLackException;
import com.github.transmitter.exception.AccountNotFoundException;
import com.github.transmitter.exception.NotAllowedTransferException;
import com.github.transmitter.model.DepositDTO;
import com.github.transmitter.model.WithdrawDTO;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@RequestScoped
@Transactional
public class TransferService {
    @Inject
    private AccountService accountService;
    @Inject
    private TransferAllowService allowService;

    @Transactional(rollbackOn = {AccountNotFoundException.class, AccountLackException.class})
    public void transfer(WithdrawDTO withdrawDTO, DepositDTO depositDTO)
            throws AccountNotFoundException, AccountLackException, NotAllowedTransferException {
        assertTransferIsAllowed(withdrawDTO, depositDTO);
        accountService.withdraw(withdrawDTO);
        accountService.deposit(depositDTO);
    }

    private void assertTransferIsAllowed(WithdrawDTO withdrawDTO, DepositDTO depositDTO) throws NotAllowedTransferException {
        allowService.allowTransfer(withdrawDTO, depositDTO);
    }
}
