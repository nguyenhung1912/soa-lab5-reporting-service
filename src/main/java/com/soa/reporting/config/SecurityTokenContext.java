package com.soa.reporting.config;

/**
 * Holds JWT token for the current request thread.
 */
public final class SecurityTokenContext {

    private static final ThreadLocal<String> CURRENT_TOKEN = new ThreadLocal<>();

    private SecurityTokenContext() {
    }

    /**
     * Saves the token in current thread.
     *
     * @param token JWT token string
     */
    public static void setToken(String token) {
        CURRENT_TOKEN.set(token);
    }

    /**
     * Gets the token from current thread.
     *
     * @return JWT token string or null
     */
    public static String getToken() {
        return CURRENT_TOKEN.get();
    }

    /**
     * Removes the token from current thread.
     */
    public static void clear() {
        CURRENT_TOKEN.remove();
    }
}
