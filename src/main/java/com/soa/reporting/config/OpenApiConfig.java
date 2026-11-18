package com.soa.reporting.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI settings with Bearer JWT auth.
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "SOA Lab 5 - Dịch vụ Báo cáo (Reporting)", version = "1.0"), security = @SecurityRequirement(name = "BearerAuth"))
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "Nhập JWT token thu được từ API POST http://localhost:8080/login")
public class OpenApiConfig {
}
