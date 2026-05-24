package com.bookashow.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "MOVIE-SERVICE")
public interface MovieClient {

    @GetMapping("/movies/{id}")
    Object getMovieById(@PathVariable("id") Long id);
}
