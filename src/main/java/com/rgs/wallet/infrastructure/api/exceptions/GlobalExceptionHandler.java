package com.rgs.wallet.infrastructure.api.exceptions;

import com.rgs.wallet.domain.enums.ErrorCodeEnum;
import com.rgs.wallet.domain.exceptions.BusinessException;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception exception, HttpServletRequest request) {
        this.logException(exception, true);
        return this.getExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, request, ErrorCodeEnum.WS500001, null);
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException exception, HttpServletRequest request) {
        this.logException(exception, false);
        return this.getExceptionResponse(exception.getStatus(), request, exception.getErrorCode(), null);
    }

    @ExceptionHandler({DuplicateRequestException.class})
    public ResponseEntity<ExceptionResponse> handleDuplicateRequestException(DuplicateRequestException exception, HttpServletRequest request) {
        this.logException(exception, false);
        return this.getExceptionResponse(HttpStatus.CONFLICT, request, ErrorCodeEnum.WS409001, null);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ExceptionResponse> handleMessageNotReadableException(final HttpMessageNotReadableException exception,
                                                                               final HttpServletRequest request){
        this.logException(exception, true);
        return this.getExceptionResponse(HttpStatus.BAD_REQUEST, request, ErrorCodeEnum.WS400001, null);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(final ConstraintViolationException exception,
                                                                                final HttpServletRequest request){
        this.logException(exception, true);
        final var fields = this.generateFields(exception.getConstraintViolations());
        return this.getExceptionResponse(HttpStatus.BAD_REQUEST, request, ErrorCodeEnum.WS400001, fields);
    }

    @ExceptionHandler({WebExchangeBindException.class})
    public ResponseEntity<ExceptionResponse> handleWebExchangeBindException(final WebExchangeBindException exception,
                                                                            final HttpServletRequest request){
        this.logException(exception, true);
        final var fields = this.generateFields(exception.getBindingResult().getFieldErrors());
        return this.getExceptionResponse(HttpStatus.BAD_REQUEST, request, ErrorCodeEnum.WS400001, fields);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception,
                                                                                   final HttpServletRequest request){
        this.logException(exception, true);
        final var fields = this.generateFields(exception.getBindingResult().getFieldErrors());
        return this.getExceptionResponse(HttpStatus.BAD_REQUEST, request, ErrorCodeEnum.WS400001, fields);
    }

    @ExceptionHandler({ServerWebInputException.class})
    public ResponseEntity<ExceptionResponse> handleServerWebInputException(final ServerWebInputException exception,
                                                                           final HttpServletRequest request){
        this.logException(exception, true);
        return this.getExceptionResponse(HttpStatus.BAD_REQUEST, request, ErrorCodeEnum.WS400001, null);
    }

    @ExceptionHandler({NoSuchMethodException.class})
    public ResponseEntity<ExceptionResponse> handleNoSuchMethodException(final NoSuchMethodException exception,
                                                                         final HttpServletRequest request){
        this.logException(exception, true);
        return this.getExceptionResponse(HttpStatus.NOT_FOUND, request, ErrorCodeEnum.WS404001, null);
    }


    private ResponseEntity<ExceptionResponse> getExceptionResponse(final HttpStatus status,
                                                                   final HttpServletRequest request,
                                                                   final ErrorCodeEnum code,
                                                                   final Map<String, List<String>> fieldErrors){
        final var response = ExceptionResponse.builder()
                .status(status.value())
                .error(this.getError(code, fieldErrors))
                .message(status.getReasonPhrase())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(response);
    }

    private Map<String, List<String>> generateFields(List<FieldError> fieldErrorList){
        if(Objects.isNull(fieldErrorList) || fieldErrorList.isEmpty()){
            return Collections.emptyMap();
        }
        return fieldErrorList.stream().collect(groupingBy(FieldError::getField,
                Collectors.mapping(DefaultMessageSourceResolvable::getDefaultMessage,
                        Collectors.toList())));
    }

    private Map<String, List<String>> generateFields(Set<ConstraintViolation<?>> constraintViolations) {
        if(Objects.isNull(constraintViolations) || constraintViolations.isEmpty()){
            return Collections.emptyMap();
        }
        return constraintViolations.stream().collect(Collectors.groupingBy(ex -> this.getLeafNode(ex.getPropertyPath()),
                Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())));
    }

    private String getLeafNode(final Path propertyPath) {
        final var iterator = propertyPath.iterator();
        var node = iterator.next();
        while(iterator.hasNext()){
            node = iterator.next();
        }
        return node.getName();
    }

    private void logException(final Exception ex, final boolean isPrintStack){
        if(isPrintStack){
            final var className = Objects.nonNull(ex) ? ex.getClass().toString() : "";
            log.error("Exception: {}", className, ex);
        }
    }

    private ExceptionErrorDetailResponse getError(final ErrorCodeEnum code,
                                                  final Map<String, List<String>> fields){
        return ExceptionErrorDetailResponse.builder()
                .message(code.getMessage(Locale.getDefault()))
                .code(code)
                .fields(fields)
                .build();
    }
}
