package com.bookashow.bookingservice.client;

import com.bookashow.bookingservice.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/notify")
    Object sendNotification(@RequestBody NotificationRequest request);
}
