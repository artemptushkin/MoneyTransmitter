package com.github.transmitter.service;

import com.github.transmitter.exception.NotAllowedTransferException;
import com.github.transmitter.model.DepositDTO;
import com.github.transmitter.model.WithdrawDTO;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransferAllowService {
    private static final String TRANSFER_TO_THE_SAME_ACCOUNT = "Transfer to the same account is not allowed (from '%d' to '%d')";

    void allowTransfer(WithdrawDTO withdrawDTO, DepositDTO depositDTO) throws NotAllowedTransferException {
        Long withdrawId = withdrawDTO.getId();
        Long depositId = depositDTO.getId();
        if (withdrawId.equals(depositId)) {
            throw toTheSameAccountException(withdrawId, depositId);
        }
    }

    private NotAllowedTransferException toTheSameAccountException(Long from, Long to) {
        return new NotAllowedTransferException(String.format(TRANSFER_TO_THE_SAME_ACCOUNT, from, to));
    }
}
