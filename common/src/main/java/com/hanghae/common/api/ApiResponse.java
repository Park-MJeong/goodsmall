package com.hanghae.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hanghae.common.exception.ErrorCode;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private static final String SUCCESS_MESSAGE = "정상적으로 처리되었습니다.";

    private final String successMessage;
    private T data;
    private final ApiError error;

    public ApiResponse(String SUCCESS_MESSAGE, T data, ApiError error) {
        this.successMessage = SUCCESS_MESSAGE;
        this.data = data;
        this.error = error;
    }


    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SUCCESS_MESSAGE,data,null);
    }


    public static ApiResponse<String> createException(int code, String message) {
        return new ApiResponse<>(null,null,new ApiError(code, message));
    }

    public static ApiResponse<?> createException(ErrorCode errorCode) {
        return new ApiResponse<>(null,null,new ApiError(errorCode.getStatusCode(),errorCode.getMessage()));
    }

    public static ApiResponse<?> createException(ErrorCode errorCode,String message) {
        return new ApiResponse<>(null,null,new ApiError(errorCode.getStatusCode(),message));
    }


}