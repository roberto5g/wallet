package com.rgs.wallet.infrastructure.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {
    @Builder.Default
    private ZonedDateTime timestamp = ZonedDateTime.now();
    private Integer status;
    private String path;
    private String message;
    private ExceptionErrorDetailResponse error;
}
