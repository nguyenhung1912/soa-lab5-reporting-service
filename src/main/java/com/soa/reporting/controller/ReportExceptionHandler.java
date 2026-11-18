package com.soa.reporting.controller;

import com.soa.reporting.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handles errors from report controllers.
 */
@RestControllerAdvice(basePackages = "com.soa.reporting.controller")
public class ReportExceptionHandler {

    /**
     * Handles invalid input or missing remote record errors.
     *
     * @param ex bad request exception
     * @return HTTP 400 error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, ex.getMessage()));
    }

    /**
     * Handles remote service connection or auth failures.
     *
     * @param ex service unavailable exception
     * @return HTTP 503 error response
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleServiceUnavailable(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(503, ex.getMessage()));
    }

    /**
     * Handles unexpected server errors.
     *
     * @param ex unexpected exception
     * @return HTTP 500 error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "Lỗi xử lý báo cáo: " + ex.getMessage()));
    }
}
