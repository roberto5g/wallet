package com.rgs.wallet.domain.exceptions;

import com.rgs.wallet.domain.enums.ErrorCodeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientFundsException extends BusinessException {
    public InsufficientFundsException() {
        super(HttpStatus.BAD_REQUEST, ErrorCodeEnum.WS400002);
    }
}