package net.thesphynx.marketplace.messaging;

import net.thesphynx.marketplace.config.RabbitConfig;
import net.thesphynx.marketplace.entity.ShopStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ShopEventProducer {

    private static final Logger log = LoggerFactory.getLogger(ShopEventProducer.class);
    private final RabbitTemplate rabbitTemplate;

    public ShopEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void shopCreated(String email, String shopName, ShopStatus status) {
        String text = "Votre boutique \"" + shopName + "\" est ouverte sur EspritMarket.";
        String json = "{\"type\":\"boutique\","
                + "\"recipient\":\"" + escape(email) + "\","
                + "\"message\":\"" + escape(text) + "\"}";
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.NOTIFICATIONS_QUEUE, json);
        } catch (Exception e) {
            // Si RabbitMQ est arrêté, la boutique est quand même créée
            log.warn("RabbitMQ indisponible, email non envoyé : {}", e.getMessage());
        }
    }

    // Protège le JSON si le nom de la boutique contient un guillemet
    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
    }
}