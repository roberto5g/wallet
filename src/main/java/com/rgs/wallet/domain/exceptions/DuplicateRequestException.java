package com.rgs.wallet.domain.exceptions;

import com.rgs.wallet.domain.enums.ErrorCodeEnum;
import org.springframework.http.HttpStatus;

public class DuplicateRequestException extends BusinessException {
    public DuplicateRequestException() {
        super(HttpStatus.CONFLICT, ErrorCodeEnum.WS409001);
    }
}
