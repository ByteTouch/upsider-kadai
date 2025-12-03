package com.example.upsider_kadai.core.config;

import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

/**
 * For the exceptions handled by {@link ResponseEntityExceptionHandler#handleException}, override those concrete handler methods called by it.<br/><br/>
 * For other exception type, add it to {@link ExceptionHandler} annotation and switch case in {@link #handleOtherExceptions} method.
 */
@RestControllerAdvice
@Slf4j
public class ErrorResponseConfig extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        var fieldErr = ex.getBindingResult().getFieldError();
        return new ResponseEntity<>(new ErrorResponse(
            "'" + fieldErr.getField() + "' " + fieldErr.getDefaultMessage()),
            ex.getHeaders(),
            ex.getStatusCode());
    }

    @Override
	protected ResponseEntity<Object> handleHandlerMethodValidationException(
			HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		return new ResponseEntity<>(new ErrorResponse(
            ex.getAllErrors()
                .stream()
                .map(msgSrcRslv -> {
                    String paramVal;
                    if (msgSrcRslv instanceof ObjectError objErr) {
                        paramVal = objErr.getObjectName();
                    } else {
                        paramVal = ((MessageSourceResolvable) msgSrcRslv.getArguments()[0]).getDefaultMessage();
                    }
                    return paramVal != null
                        ? "'" + paramVal + "' " + msgSrcRslv.getDefaultMessage()
                        : "Unknown validation error. Please contact to the system maintainer.";
                })
                .collect(Collectors.joining(". "))),
            ex.getHeaders(),
            ex.getStatusCode());
	}

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        if (ex.getCause() instanceof InvalidFormatException vie
            && vie.getTargetType() != null
            && vie.getTargetType().isEnum()) {

            return new ResponseEntity<>(
                new ErrorResponse(String.format("Invalid enum value '%s' for field '%s'.", vie.getValue(), vie.getPath().getLast().getFieldName())),
                headers,
                HttpStatusCode.valueOf(400));
        }
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    @ExceptionHandler({
        ConstraintViolationException.class,
        ResponseStatusException.class
    })
    ResponseEntity<ErrorResponse> handleOtherExceptions(Exception e) {
        return switch (e) {
            case ConstraintViolationException ex -> new ResponseEntity<>(
                new ErrorResponse(ex.getMessage()),
                HttpStatus.BAD_REQUEST);
            case ResponseStatusException ex -> new ResponseEntity<>(
                new ErrorResponse(ex.getReason()),
                ex.getStatusCode());
            default -> {
                log.error("Unimplemented exception type: " + e.getClass());
                yield new ResponseEntity<>(
                    new ErrorResponse("Please contact to the system maintainer"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }
        };
    }
    public record ErrorResponse(
        @Nullable String message
    ) {}
}
