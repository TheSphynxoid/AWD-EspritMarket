package net.thesphynx.patnership.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    /**
     * File de destination pour le service Notification.
     * Durable = true (identique à la configuration du service Notification).
     */
    public static final String NOTIFICATIONS_QUEUE = "notifications";

    @Bean
    public Queue notificationsQueue() {
        return new Queue(NOTIFICATIONS_QUEUE, true);
    }
}

