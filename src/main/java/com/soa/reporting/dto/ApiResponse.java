package com.soa.reporting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standard API response wrapper for messages and data.
 *
 * @param <T> type of response data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private int status;
    private String message;
    private T data;

    /**
     * Creates an empty API response.
     */
    public ApiResponse() {
    }

    /**
     * Creates an API response without data.
     *
     * @param success true if request succeeded
     * @param status HTTP status code
     * @param message response message
     */
    public ApiResponse(boolean success, int status, String message) {
        this.success = success;
        this.status = status;
        this.message = message;
    }

    /**
     * Creates an API response with data.
     *
     * @param success true if request succeeded
     * @param status HTTP status code
     * @param message response message
     * @param data response payload
     */
    public ApiResponse(boolean success, int status, String message, T data) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     * Builds a 200 OK response.
     *
     * @param message success message
     * @param data response payload
     * @param <T> payload type
     * @return API response object
     */
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, 200, message, data);
    }

    /**
     * Builds a 201 Created response.
     *
     * @param message success message
     * @param data created payload
     * @param <T> payload type
     * @return API response object
     */
    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(true, 201, message, data);
    }

    /**
     * Builds an error response.
     *
     * @param status HTTP error code
     * @param message error message
     * @param <T> payload type
     * @return API response object
     */
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(false, status, message);
    }

    /**
     * Checks if request succeeded.
     *
     * @return true if successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets success flag.
     *
     * @param success success flag
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets HTTP status code.
     *
     * @return status code
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets HTTP status code.
     *
     * @param status status code
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets response message.
     *
     * @return message text
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets response message.
     *
     * @param message message text
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets response payload.
     *
     * @return payload data
     */
    public T getData() {
        return data;
    }

    /**
     * Sets response payload.
     *
     * @param data payload data
     */
    public void setData(T data) {
        this.data = data;
    }
}
