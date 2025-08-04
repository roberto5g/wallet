package com.rgs.wallet.domain.exceptions;

import com.rgs.wallet.domain.enums.ErrorCodeEnum;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private static final long serialVersion = 1L;
    private final ErrorCodeEnum errorCode;
    private final HttpStatus status;

    protected BusinessException(HttpStatus status, ErrorCodeEnum errorCode) {
        this.errorCode = errorCode;
        this.status = status;
    }

}
