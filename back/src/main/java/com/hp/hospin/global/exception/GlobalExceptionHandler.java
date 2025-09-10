package com.hp.hospin.global.exception;

import com.hp.hospin.global.apiResponse.ApiResponse;
import com.hp.hospin.global.standard.base.Empty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Empty> handleValidException(MethodArgumentNotValidException e) {
        List<String> errorMessages = e.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .toList();

        log.error(errorMessages.toString());

        CustomHttpStatus customHttpStatus = CustomHttpStatus.builder()
                .statusCode(400)
                .statusMessage(errorMessages.get(0))   // 클라이언트로 첫 메세지만을 반환
                .build();

        return ApiResponse.validationException(customHttpStatus);
    }

    @ExceptionHandler(HospinException.class)
    public ApiResponse<Empty> handleCustomException(HospinException hospinException) {
        log.error(hospinException.getErrorCode() + ": " + hospinException.getMessage());

        return ApiResponse.customException(hospinException.getErrorCode());
    }
}
