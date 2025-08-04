package com.rgs.wallet.domain.exceptions;

import com.rgs.wallet.domain.enums.ErrorCodeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WalletNotFoundException extends BusinessException {
    public WalletNotFoundException() {
        super(
                HttpStatus.NOT_FOUND,
                ErrorCodeEnum.WS404001
        );
    }
}
