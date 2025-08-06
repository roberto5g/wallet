package com.rgs.wallet.domain.exceptions;

import com.rgs.wallet.domain.enums.ErrorCodeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SameWalletTransferException extends BusinessException {
    public SameWalletTransferException() {
        super(HttpStatus.CONFLICT, ErrorCodeEnum.WS409003);
    }
}