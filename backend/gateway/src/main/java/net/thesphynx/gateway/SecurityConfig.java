package net.thesphynx.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Sécurité centralisée (token based) : la gateway valide le JWT émis par Keycloak
 * (signature via le JWK set du realm) avant de router la requête. Le header
 * Authorization est transmis tel quel aux microservices.
 *
 * Routes publiques : inscription, catalogue en lecture (services, marketplace,
 * événements), documentation Swagger et health. Tout le reste exige un token.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .cors(Customizer.withDefaults())
                .authorizeExchange(ex -> ex
                        // Preflight CORS
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Les actuators des microservices ne sont jamais exposés via la gateway
                        .pathMatchers("/*/actuator/**").denyAll()
                        // Health + métriques de la gateway (scrapées par Prometheus sur le réseau interne)
                        .pathMatchers("/actuator/health/**", "/actuator/health", "/actuator/prometheus").permitAll()
                        // Swagger centralisé
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/webjars/**",
                                "/v3/api-docs/**", "/docs/**").permitAll()
                        // Compte utilisateur
                        .pathMatchers(HttpMethod.POST, "/user/register").permitAll()
                        .pathMatchers("/user/health").permitAll()
                        // Panier : propre à chaque utilisateur, jamais public
                        .pathMatchers("/mic2/paniers/**").authenticated()
                        // Catalogue public en lecture
                        .pathMatchers(HttpMethod.GET, "/srv/**", "/mic2/**", "/mic1/events/**",
                                "/patnership/api/partnership/companies/**",
                                "/patnership/api/partnership/job-offers/**").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}
