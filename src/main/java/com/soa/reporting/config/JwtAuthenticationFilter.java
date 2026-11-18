package com.soa.reporting.config;

import com.soa.reporting.dto.AuthResponse;
import com.soa.reporting.service.AuthClientService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter that checks JWT token on each request.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthClientService authClientService;

    /**
     * Creates filter with AuthClientService.
     *
     * @param authClientService client used to validate token
     */
    public JwtAuthenticationFilter(AuthClientService authClientService) {
        this.authClientService = authClientService;
    }

    /**
     * Validates Bearer token and sets user authentication context.
     *
     * @param request incoming HTTP request
     * @param response outgoing HTTP response
     * @param filterChain next filters in chain
     * @throws ServletException when servlet fails
     * @throws IOException when IO fails
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        try {
            if (authHeader != null && !authHeader.trim().isEmpty()) {
                String token = authHeader.trim().startsWith("Bearer ")
                        ? authHeader.trim().substring(7).trim()
                        : authHeader.trim();
                SecurityTokenContext.setToken(token);

                AuthResponse authResponse = authClientService.validateTokenWithAuthService(authHeader);

                if (authResponse.isSuccess() && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String username = authResponse.getUserName() != null ? authResponse.getUserName() : "authenticatedUser";

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.emptyList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    request.setAttribute("authErrorMessage", authResponse.getMessage());
                }
            } else {
                request.setAttribute("authErrorMessage",
                        "Vui lòng cung cấp JWT Token trong Header 'Authorization: Bearer <token>'");
            }

            filterChain.doFilter(request, response);
        } finally {
            SecurityTokenContext.clear();
        }
    }
}
