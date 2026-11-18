package com.soa.reporting.dto;

/**
 * Data object for response from Auth Service.
 */
public class AuthResponse {

    private boolean success;
    private String message;
    private String token;
    private String userName;

    /**
     * Creates an empty auth response.
     */
    public AuthResponse() {
    }

    /**
     * Creates an auth response with status and message.
     *
     * @param success true if token is valid
     * @param message response message text
     */
    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Creates an auth response with full fields.
     *
     * @param success true if token is valid
     * @param message response message text
     * @param token JWT token string
     * @param userName authenticated user name
     */
    public AuthResponse(boolean success, String message, String token, String userName) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.userName = userName;
    }

    /**
     * Checks if auth was successful.
     *
     * @return true if valid
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets auth success flag.
     *
     * @param success success flag
     */
    public void setSuccess(boolean success) {
        this.success = success;
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
     * Gets JWT token string.
     *
     * @return token value
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets JWT token string.
     *
     * @param token token value
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets authenticated user name.
     *
     * @return user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets authenticated user name.
     *
     * @param userName user name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
