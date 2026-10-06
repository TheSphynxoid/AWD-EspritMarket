package esprit.srv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SrvApplication {

    public static void main(String[] args) {
        SpringApplication.run(SrvApplication.class, args);
    }

}
