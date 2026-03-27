package com.hp.hospin.global.apiResponse;

import com.hp.hospin.global.exception.CustomHttpStatus;
import com.hp.hospin.global.exception.ErrorCode;
import com.hp.hospin.global.standard.base.Empty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.net.URI;

@Getter
public class ApiResponse<T> {
    private final int statusCode;
    @NotNull
    private final String message;
    @NotNull
    private final ApiResultType resultType;
    private final String errorCode;
    @NotNull
    private final T data;

    private ApiResponse(int statusCode, String message, ApiResultType resultType, T data) {
        this(statusCode, message, resultType, null, data);
    }

    private ApiResponse(int statusCode, String message, ApiResultType resultType, String errorCode, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.resultType = resultType;
        this.errorCode = errorCode;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                HttpStatus.OK.getReasonPhrase(),
                ApiResultType.SUCCESS,
                data
        );
    }

    public static ApiResponse<Empty> created() {
        return new ApiResponse<>(
                HttpStatus.CREATED.value(),
                HttpStatus.CREATED.getReasonPhrase(),
                ApiResultType.SUCCESS,
                new Empty()
        );
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(
                HttpStatus.CREATED.value(),
                HttpStatus.CREATED.getReasonPhrase(),
                ApiResultType.SUCCESS,
                data
        );
    }

    public static ApiResponse<URI> created(String uri) {
        URI newUri = URI.create(uri);
        return new ApiResponse<>(
                HttpStatus.CREATED.value(),
                HttpStatus.CREATED.getReasonPhrase(),
                ApiResultType.SUCCESS,
                newUri
        );
    }

    public static ApiResponse<Empty> noContent() {
        return new ApiResponse<>(
                HttpStatus.NO_CONTENT.value(),
                HttpStatus.NO_CONTENT.getReasonPhrase(),
                ApiResultType.SUCCESS,
                new Empty()
        );
    }

    public static ApiResponse<Empty> validationException(CustomHttpStatus customHttpStatus) {
        return new ApiResponse<>(
                customHttpStatus.getStatusCode(),
                customHttpStatus.getStatusMessage(),
                ApiResultType.VALIDATION_EXCEPTION,
                new Empty()
        );
    }

    public static ApiResponse<Empty> customException(ErrorCode errorCode) {
        return new ApiResponse<>(
                errorCode.getStatus().value(),
                errorCode.getMessage(),
                ApiResultType.CUSTOM_EXCEPTION,
                errorCode.name(),
                new Empty()
        );
    }

    public static ApiResponse<Empty> unauthorized() {
        return new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),          // 401
                HttpStatus.UNAUTHORIZED.getReasonPhrase(), // "Unauthorized"
                ApiResultType.CUSTOM_EXCEPTION,           // FAIL 타입으로 설정 가능
                new Empty()
        );
    }

    public static ApiResponse<Empty> impossible() {
        return new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ApiResultType.IMPOSSIBLE,
                new Empty()
        );
    }

    public static <T> ApiResponse<T> impossible(T data) {
        return new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ApiResultType.IMPOSSIBLE,
                data
        );
    }
}
