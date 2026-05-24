package com.bookashow.bookingservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI bookingServiceOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url("http://localhost:8080").description("API Gateway")))
                .info(new Info()
                        .title("Booking Service API")
                        .description("REST API for managing bookings in BookAShow platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("BookAShow Team")
                                .email("support@bookashow.com")));
    }
}
