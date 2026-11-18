package com.soa.reporting.service;

import com.soa.reporting.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/**
 * Service that checks JWT tokens with Auth Service.
 */
@Service
public class AuthClientService {

    private final RestClient restClient;

    @Value("${auth.service.url:http://localhost:8080/auth}")
    private String authServiceUrl;

    /**
     * Creates AuthClientService with RestClient.
     *
     * @param restClient HTTP client for remote calls
     */
    public AuthClientService(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Sends token to Auth Service to check if it is valid.
     *
     * @param authHeaderOrToken raw token or Bearer header
     * @return auth response from Auth Service
     */
    public AuthResponse validateTokenWithAuthService(String authHeaderOrToken) {
        if (authHeaderOrToken == null || authHeaderOrToken.trim().isEmpty()) {
            return new AuthResponse(false, "Thiếu token xác thực trong Header Authorization");
        }

        String headerValue = authHeaderOrToken.trim();
        if (!headerValue.startsWith("Bearer ")) {
            headerValue = "Bearer " + headerValue;
        }

        try {
            AuthResponse response = restClient.get()
                    .uri(authServiceUrl)
                    .header(HttpHeaders.AUTHORIZATION, headerValue)
                    .retrieve()
                    .body(AuthResponse.class);

            if (response != null) {
                return response;
            }
            return new AuthResponse(false, "Phản hồi rỗng từ Auth Service");
        } catch (HttpClientErrorException.Unauthorized e) {
            AuthResponse errorBody = e.getResponseBodyAs(AuthResponse.class);
            if (errorBody != null && errorBody.getMessage() != null) {
                return errorBody;
            }
            return new AuthResponse(false, "Token không hợp lệ hoặc đã hết hạn");
        } catch (Exception e) {
            return new AuthResponse(false, "Không thể kết nối tới Auth Service (localhost:8080). Vui lòng đảm bảo Auth Service đang chạy.");
        }
    }
}
