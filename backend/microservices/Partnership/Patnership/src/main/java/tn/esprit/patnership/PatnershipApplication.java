package tn.esprit.patnership;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class PatnershipApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatnershipApplication.class, args);
    }

}
