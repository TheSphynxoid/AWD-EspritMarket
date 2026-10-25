package net.thesphynx.event.service;

import net.thesphynx.event.client.NotificationClient;
import net.thesphynx.event.dto.EventDto;
import net.thesphynx.event.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EventNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EventNotificationService.class);

    private final NotificationClient notificationClient;

    public EventNotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @Async
    public void notifyEventCreated(EventDto event) {
        try {
            String recipient = (event.getUserId() == null)
                    ? "admin@espritmarket.local"
                    : "user" + event.getUserId() + "@espritmarket.local";
            String message = "Un nouvel événement '" + event.getName() + "' a été créé.";
            notificationClient.createNotification(new NotificationRequest(recipient, message, "event"));
        } catch (Exception e) {
            log.warn("Failed to send event notification: {}", e.getMessage());
        }
    }
}