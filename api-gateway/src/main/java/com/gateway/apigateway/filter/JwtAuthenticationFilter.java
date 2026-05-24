//package com.gateway.apigateway.filter;
//
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import reactor.core.publisher.Mono;
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//
//import java.security.Key;
//
//@Component
//public class JwtAuthenticationFilter implements WebFilter {
//
//    private static final String SECRET =
//        "cb2f5c70eafc6989b9755a8d69996727376c5658006b92a0cb4f330bc439e598";
//
//    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//
//        ServerHttpRequest request = exchange.getRequest();
//        String path = request.getURI().getPath();
//
//        // ✅ 1️⃣ Allow auth endpoints without JWT
//        if (path.startsWith("/auth")) {
//            return chain.filter(exchange);
//        }
//
//        // ✅ 2️⃣ Allow actuator (optional but recommended)
//        if (path.startsWith("/actuator")) {
//            return chain.filter(exchange);
//        }
//
//        // ✅ 3️⃣ Enforce JWT for movie and play services
//        if (path.startsWith("/movies") || path.startsWith("/plays")) {
//
//            String authHeader =
//                request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                return unauthorized(exchange);
//            }
//
//            String token = authHeader.substring(7);
//
//            try {
//                Jwts.parserBuilder()
//                        .setSigningKey(key)
//                        .build()
//                        .parseClaimsJws(token);
//            } catch (JwtException e) {
//                return unauthorized(exchange);
//            }
//        }
//
//        // ✅ 4️⃣ All checks passed
//        return chain.filter(exchange);
//    }
//
//    private Mono<Void> unauthorized(ServerWebExchange exchange) {
//        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//        return exchange.getResponse().setComplete();
//    }
//}



package com.gateway.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.security.Key;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    private final Key key;

    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Public endpoints — no JWT needed
        if (path.startsWith("/auth")
                || path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars")) {
            return chain.filter(exchange);
        }

        // Protected endpoints — validate JWT and forward role
        if (path.startsWith("/movies")
                || path.startsWith("/plays")
                || path.startsWith("/bookings")
                || path.startsWith("/notify")
                || path.startsWith("/payments")) {

            String authHeader =
                exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange);
            }

            String token = authHeader.substring(7);

            Claims claims;
            try {
                claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            } catch (JwtException e) {
                return unauthorized(exchange);
            }

            String role = claims.get("role", String.class);// extraxt the role
            String username = claims.getSubject();

            // Forward role and username as headers to downstream services
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Role", role != null ? role : "") // add role 
                    .header("X-User-Name", username != null ? username : "") //add useranme 
                    .build();
 // know user identity and apply logic based on that
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        // Sends modified request to service
        }

        return chain.filter(exchange); //f no conditions matched, then forward request
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
