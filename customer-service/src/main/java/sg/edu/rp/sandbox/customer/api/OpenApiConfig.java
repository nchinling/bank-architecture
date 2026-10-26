package sg.edu.rp.sandbox.customer.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI metadata for the Customer Service. Declares the bearer-token security
 * scheme so Swagger UI shows an Authorize button.
 */
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Customer Service API",
        version = "v1",
        description = "Customer onboarding and simulated eKYC for the RP Digital Bank Sandbox."))
@SecurityScheme(
        name = "bearer-jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class OpenApiConfig {
}
