//package com.gateway.apigateway.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//
//        return http
//            .csrf(ServerHttpSecurity.CsrfSpec::disable)
//
//            .authorizeExchange(exchange -> exchange
//
//                // ✅ Public endpoints
//                .pathMatchers("/auth/**").permitAll()
//                .pathMatchers("/actuator/**").permitAll()
//
//                // ✅ Explicitly allow all microservices (important clarity)
//                .pathMatchers("/movies/**").permitAll()
//                .pathMatchers("/plays/**").permitAll()
//                .pathMatchers("/bookings/**").permitAll()
//                .pathMatchers("/notify/**").permitAll()
//
//                // ✅ Everything else allowed (for now phase 1)
//                .anyExchange().permitAll()
//            )
//
//            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
//            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
//
//            .build();
//    }
//}



//package com.gateway.apigateway.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//
//        return http
//            .csrf(ServerHttpSecurity.CsrfSpec::disable)
//
//            // ✅ THIS IS THE KEY FIX
//            .authorizeExchange(exchange -> exchange
//                .anyExchange().permitAll()
//            )
//
//            // ✅ disable all authentication mechanisms
//            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
//            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
//
//            .build();
//    }
//}


//package com.gateway.apigateway.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//
//        return http
//
//            // ✅ Disable CSRF
//            .csrf(ServerHttpSecurity.CsrfSpec::disable)
//
//            // ✅ IMPORTANT: DO NOT define path-specific rules here
//            // Let JWT filter handle everything
//            .authorizeExchange(exchange -> exchange
//                .anyExchange().permitAll()
//            )
//
//            // ✅ Disable default authentication
//            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
//            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
//            .logout(ServerHttpSecurity.LogoutSpec::disable)
//
//            .build();
//    }
//}

package com.gateway.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(ServerHttpSecurity.CorsSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .logout(ServerHttpSecurity.LogoutSpec::disable)
            .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
            .build();
    }
}