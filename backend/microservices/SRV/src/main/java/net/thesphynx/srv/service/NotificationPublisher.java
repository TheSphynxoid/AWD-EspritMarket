package net.thesphynx.srv.service;

import net.thesphynx.srv.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Communication asynchrone : publie une notification dans la file RabbitMQ "notifications"
 * (consommée par le microservice Notification). Un échec d'envoi ne bloque jamais l'action métier.
 */
@Component
public class NotificationPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public NotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(String recipientEmail, String recipientId, String message) {
        String recipient = recipientEmail != null && !recipientEmail.isBlank()
                ? recipientEmail
                : "user-" + recipientId + "@espritmarket.local";
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.NOTIFICATION_QUEUE,
                    Map.of("recipient", recipient, "message", message, "type", "booking"));
        } catch (Exception e) {
            LOGGER.warn("Notification non envoyée à {} : {}", recipient, e.getMessage());
        }
    }
}
