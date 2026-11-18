package com.soa.reporting.service;

import com.soa.reporting.config.SecurityTokenContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

/**
 * Shared helper for remote REST calls with Bearer token.
 */
final class ServiceClientSupport {

    private ServiceClientSupport() {
    }

    /**
     * Builds the Authorization header value from token or thread context.
     *
     * @param explicitToken optional token string
     * @return Bearer header value or null
     */
    static String resolveBearerHeader(String explicitToken) {
        String token = (explicitToken != null && !explicitToken.trim().isEmpty())
                ? explicitToken.trim()
                : SecurityTokenContext.getToken();

        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        return token.startsWith("Bearer ") ? token : "Bearer " + token;
    }

    /**
     * Executes a GET call and maps response to a class type.
     *
     * @param restClient HTTP client
     * @param url target endpoint URL
     * @param bearerToken optional token
     * @param responseType expected Java class
     * @param notFoundMessage message when 404 happens
     * @param serviceLabel remote service display name
     * @param <T> response type
     * @return deserialized response object
     */
    static <T> T getObject(RestClient restClient,
            String url,
            String bearerToken,
            Class<T> responseType,
            String notFoundMessage,
            String serviceLabel) {
        return executeGet(restClient, url, bearerToken, notFoundMessage, serviceLabel,
                spec -> spec.retrieve().body(responseType));
    }

    /**
     * Executes a GET call and maps response to a generic type.
     *
     * @param restClient HTTP client
     * @param url target endpoint URL
     * @param bearerToken optional token
     * @param responseType expected generic type reference
     * @param serviceLabel remote service display name
     * @param <T> response type
     * @return deserialized response object
     */
    static <T> T getParameterized(RestClient restClient,
            String url,
            String bearerToken,
            ParameterizedTypeReference<T> responseType,
            String serviceLabel) {
        return executeGet(restClient, url, bearerToken, null, serviceLabel,
                spec -> spec.retrieve().body(responseType));
    }

    private static <T> T executeGet(RestClient restClient,
            String url,
            String bearerToken,
            String notFoundMessage,
            String serviceLabel,
            Function<RestClient.RequestHeadersSpec<?>, T> responseReader) {
        String authHeader = resolveBearerHeader(bearerToken);
        try {
            RestClient.RequestHeadersSpec<?> requestSpec = restClient.get().uri(url);
            if (authHeader != null) {
                requestSpec.header(HttpHeaders.AUTHORIZATION, authHeader);
            }
            return responseReader.apply(requestSpec);
        } catch (HttpClientErrorException.NotFound e) {
            String message = notFoundMessage != null
                    ? notFoundMessage
                    : "Không tìm thấy tài nguyên trên " + serviceLabel;
            throw new IllegalArgumentException(message);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new IllegalStateException(
                    "Không có quyền truy cập " + serviceLabel + ": Token không hợp lệ hoặc đã hết hạn");
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Không thể kết nối hoặc đọc dữ liệu từ " + serviceLabel + " tại " + url + ": " + e.getMessage());
        }
    }
}
