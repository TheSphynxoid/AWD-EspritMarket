package net.thesphynx.event.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Documentation OpenAPI exposée sur /v3/api-docs et agrégée par la gateway
 * (Swagger centralisé). Le "server" pointe vers la gateway pour que
 * "Try it out" passe par la sécurité centralisée (bouton Authorize = JWT Keycloak).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi(@Value("${openapi.server-url:http://localhost:8088}") String serverUrl) {
        return new OpenAPI()
                .info(new Info().title("Event API").version("v1"))
                .servers(List.of(new Server().url(serverUrl).description("API Gateway")))
                .components(new Components().addSecuritySchemes("bearer-jwt",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
