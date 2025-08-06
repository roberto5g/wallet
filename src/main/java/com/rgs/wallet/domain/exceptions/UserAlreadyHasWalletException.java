package com.rgs.wallet.domain.exceptions;

import com.rgs.wallet.domain.enums.ErrorCodeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyHasWalletException extends BusinessException {
    public UserAlreadyHasWalletException() {
        super(HttpStatus.CONFLICT, ErrorCodeEnum.WS409002);
    }
}
