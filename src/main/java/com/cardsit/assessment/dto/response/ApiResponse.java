package com.cardsit.assessment.dto.response;

import java.time.LocalDateTime;

/**
 * Standard API response envelope used by every endpoint.
 *
 * <ul>
 *   <li>{@code success = true}  → {@code description} explains what succeeded; {@code data} holds the payload</li>
 *   <li>{@code success = false} → {@code description} explains the failure; {@code data} is null</li>
 * </ul>
 *
 * <p>Use factory helpers {@link #ok(String, Object)} and {@link #fail(String)}
 * instead of constructing manually — keeps timestamps consistent.
 *
 * @param <T> type of the {@code data} payload
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

    /** Successful response with a human-readable description and payload. */
    public static <T> ApiResponse<T> ok(String description, T data) {
        return new ApiResponse<>(true, description, data, LocalDateTime.now());
    }

    /** Failed response — typically used by {@code GlobalExceptionHandler}. */
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
