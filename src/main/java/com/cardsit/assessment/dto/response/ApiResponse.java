package com.cardsit.assessment.dto.response;

import java.time.LocalDateTime;

/**
 * Standard API envelope.
 * - success = true  → description explains what succeeded
 * - success = false → description explains why it failed
 */
public class ApiResponse<T> {

    private boolean success;
    private String description;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, String description, T data, LocalDateTime timestamp) {
        this.success = success;
        this.description = description;
        this.data = data;
        this.timestamp = timestamp;
    }

    public static <T> ApiResponse<T> ok(String description, T data) {
        return new ApiResponse<>(true, description, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> fail(String description) {
        return new ApiResponse<>(false, description, null, LocalDateTime.now());
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
