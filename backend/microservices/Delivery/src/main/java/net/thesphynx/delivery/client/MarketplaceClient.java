package net.thesphynx.delivery.client;

import net.thesphynx.delivery.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Communication synchrone (REST via Eureka) avec le microservice marketplace. */
@FeignClient(name = "marketplace")
public interface MarketplaceClient {

    @GetMapping("/mic2/products/{id}")
    ProductDto getProduct(@PathVariable("id") Long id);
}
