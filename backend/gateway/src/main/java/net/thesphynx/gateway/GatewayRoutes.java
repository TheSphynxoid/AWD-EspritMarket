package net.thesphynx.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Defines the gateway routes.
 *
 * Configuration dynamique avec Eureka : les URI "lb://NOM-DU-SERVICE" chargent le
 * load balancer de Spring Cloud, qui interroge Eureka pour trouver une instance
 * disponible. Aucune URL n'est codée en dur.
 */
@Configuration
public class GatewayRoutes {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {

        // Configuration statique (sans Eureka) — pour comparaison :
        // return builder.routes()
        //         .route("srv", r -> r.path("/srv/**")
        //                 .uri("http://localhost:8082"))
        //         .route("marketplace", r -> r.path("/marketplace/**")
        //                 .uri("http://localhost:8083"))
        //         .route("delivery", r -> r.path("/delivery/**")
        //                 .uri("http://localhost:8084"))
        //         .route("event", r -> r.path("/event/**")
        //                 .uri("http://localhost:8085"))
        //         .build();

        return builder.routes()

                .route("srv", r -> r.path("/srv/**")
                        .uri("lb://SRV"))

                .route("marketplace", r -> r.path("/marketplace/**")
                        .uri("lb://MARKETPLACE"))

                .route("delivery", r -> r.path("/delivery/**")
                        .uri("lb://DELIVERY"))

                .route("event", r -> r.path("/event/**")
                        .uri("lb://EVENT"))

                .route("patnership", r -> r.path("/patnership/**")
                        .uri("lb://PATNERSHIP"))

                .build();
    }
}
