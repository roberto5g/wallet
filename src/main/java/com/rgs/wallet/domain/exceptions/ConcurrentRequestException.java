package com.rgs.wallet.domain.exceptions;

import com.rgs.wallet.domain.enums.ErrorCodeEnum;
import org.springframework.http.HttpStatus;

public class ConcurrentRequestException extends BusinessException {
    public ConcurrentRequestException() {
        super(HttpStatus.TOO_MANY_REQUESTS, ErrorCodeEnum.WS429001);
    }
}
