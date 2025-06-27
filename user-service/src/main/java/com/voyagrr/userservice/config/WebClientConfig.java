package com.voyagrr.userservice.config;

import com.voyagrr.userservice.utility.KeycloakUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final KeycloakProperties keycloakProperties;
    private final KeycloakUtility keycloakUtility;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean(name = "keycloakWebClient")
    public WebClient keycloakWebClient() {
        return WebClient.builder()
                .baseUrl(keycloakProperties.getKeycloakUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "keycloakAuthWebClient")
    public WebClient keycloakAuthWebClient(@Qualifier("keycloakWebClient") WebClient tokenClient) {
        return WebClient.builder()
                .baseUrl(keycloakProperties.getKeycloakUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter((request, next) -> {
                    ClientRequest newRequest = ClientRequest.from(request)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + keycloakUtility.getAccessToken(tokenClient))
                            .build();
                    return next.exchange(newRequest);
                })
                .build();
    }


}
