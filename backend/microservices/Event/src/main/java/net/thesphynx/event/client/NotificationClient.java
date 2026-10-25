package net.thesphynx.event.client;

import net.thesphynx.event.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION")
public interface NotificationClient {

    @PostMapping("/notification")
    void createNotification(@RequestBody NotificationRequest request);
}