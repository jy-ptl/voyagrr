package com.voyagrr.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()

                        .pathMatchers("/user-service/actuator/**").permitAll()
                        .pathMatchers("/storage-service/actuator/**").permitAll()
                        .pathMatchers("/sharing-service/actuator/**").permitAll()
                        .pathMatchers("/encoding-service/actuator/**").permitAll()

                        .pathMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .anyExchange().authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));

        return http.build();
    }

}
