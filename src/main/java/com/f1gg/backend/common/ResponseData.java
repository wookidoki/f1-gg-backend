package com.f1gg.backend.common;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData<T> {
    private boolean success;
    private int status;
    private String message;
    private T data;

    // 성공 응답 (데이터 있음)
    public static <T> ResponseData<T> success(T data) {
        return ResponseData.<T>builder()
                .success(true)
                .status(200)
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    // 성공 응답 (커스텀 메시지)
    public static <T> ResponseData<T> success(T data, String message) {
        return ResponseData.<T>builder()
                .success(true)
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    // 생성 성공 (201)
    public static <T> ResponseData<T> created(T data) {
        return ResponseData.<T>builder()
                .success(true)
                .status(201)
                .message("리소스가 성공적으로 생성되었습니다.")
                .data(data)
                .build();
    }

    // 실패 응답
    public static <T> ResponseData<T> fail(int status, String message) {
        return ResponseData.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .data(null)
                .build();
    }

    // 400 Bad Request
    public static <T> ResponseData<T> badRequest(String message) {
        return fail(400, message);
    }

    // 401 Unauthorized
    public static <T> ResponseData<T> unauthorized(String message) {
        return fail(401, message);
    }

    // 403 Forbidden
    public static <T> ResponseData<T> forbidden(String message) {
        return fail(403, message);
    }

    // 404 Not Found
    public static <T> ResponseData<T> notFound(String message) {
        return fail(404, message);
    }

    // 500 Internal Server Error
    public static <T> ResponseData<T> error(String message) {
        return fail(500, message);
    }
}
