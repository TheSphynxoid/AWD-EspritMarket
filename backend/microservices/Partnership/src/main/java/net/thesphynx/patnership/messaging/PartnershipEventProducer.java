package net.thesphynx.patnership.messaging;

import net.thesphynx.patnership.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PartnershipEventProducer {

    private static final Logger log = LoggerFactory.getLogger(PartnershipEventProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public PartnershipEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Envoie un message asynchrone dans la file RabbitMQ "notifications"
     * lorsqu'une nouvelle entreprise partenaire est créée.
     *
     * @param email Email de contact du destinataire
     * @param companyName Nom de l'entreprise partenaire
     * @param sector Secteur d'activité
     */
    public void notifyCompanyCreated(String email, String companyName, String sector) {
        String recipient = (email != null && !email.isBlank()) ? email : "admin@espritmarket.tn";
        String sectorInfo = (sector != null && !sector.isBlank()) ? " (Secteur : " + sector + ")" : "";
        String text = "L'entreprise partenaire \"" + companyName + "\"" + sectorInfo + " a été ajoutée avec succès sur EspritMarket.";

        String json = "{\"type\":\"partnership\","
                + "\"recipient\":\"" + escape(recipient) + "\","
                + "\"message\":\"" + escape(text) + "\"}";

        try {
            rabbitTemplate.convertAndSend(RabbitConfig.NOTIFICATIONS_QUEUE, json);
            log.info("Message RabbitMQ envoyé avec succès pour l'entreprise : {}", companyName);
        } catch (Exception e) {
            // Résilience : Si RabbitMQ est indisponible, l'entreprise est quand même créée
            log.warn("RabbitMQ indisponible, notification non envoyée pour l'entreprise {} : {}", companyName, e.getMessage());
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
    }
}

