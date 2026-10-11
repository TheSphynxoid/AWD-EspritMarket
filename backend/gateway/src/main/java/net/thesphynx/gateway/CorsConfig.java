package net.thesphynx.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * Configuration CORS (Cross-Origin Resource Sharing).
 *
 * Le navigateur bloque par défaut les requêtes d'une origine différente
 * (ex. frontend Angular sur localhost:4200 qui appelle la gateway sur
 * localhost:8080). Ce filtre autorise explicitement ces requêtes.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {

        CorsConfiguration corsConfig = new CorsConfiguration();

        corsConfig.setAllowedOrigins(java.util.Arrays.asList("http://localhost:4200"));

        corsConfig.setAllowedMethods(
                java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        corsConfig.setAllowedHeaders(java.util.Arrays.asList("*"));

        corsConfig.setAllowCredentials(false);

        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource(new PathPatternParser());

        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
