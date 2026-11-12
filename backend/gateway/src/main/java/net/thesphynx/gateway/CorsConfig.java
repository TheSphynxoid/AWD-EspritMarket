package net.thesphynx.gateway;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * Configuration CORS (Cross-Origin Resource Sharing).
 *
 * Le navigateur bloque par défaut les requêtes d'une origine différente
 * (ex. frontend Angular sur localhost:4200 qui appelle la gateway sur
 * localhost:8088). Cette source est utilisée par Spring Security (http.cors()).
 *
 * En Docker, le frontend passe par nginx (même origine) : CORS n'est alors plus
 * nécessaire. Origines supplémentaires : variable GATEWAY_CORS_ORIGINS
 * (liste séparée par des virgules).
 */
@Configuration
public class CorsConfig {

    @Value("${gateway.cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration corsConfig = new CorsConfiguration();

        corsConfig.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList());

        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        corsConfig.setAllowedHeaders(List.of("*"));

        corsConfig.setAllowCredentials(false);

        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource(new PathPatternParser());

        source.registerCorsConfiguration("/**", corsConfig);

        return source;
    }
}
