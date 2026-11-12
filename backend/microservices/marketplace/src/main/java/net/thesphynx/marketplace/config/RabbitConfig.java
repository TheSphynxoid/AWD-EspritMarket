package net.thesphynx.marketplace.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Même nom que celui lu par le service Notification (Python)
    public static final String NOTIFICATIONS_QUEUE = "notifications";

    @Bean
    public Queue notificationsQueue() {
        return new Queue(NOTIFICATIONS_QUEUE, true);   // durable, comme côté Python
    }
}