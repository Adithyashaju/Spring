package com.bookashow.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PLAY-SERVICE1")
public interface PlayClient {

    @GetMapping("/plays/{id}")
    Object getPlayById(@PathVariable("id") Long id);
}
