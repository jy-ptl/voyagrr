package com.voyagrr.userservice.utility;

import com.voyagrr.userservice.config.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class KeycloakUtility {

    private final KeycloakProperties keycloakProperties;

    private String cachedToken;
    private Instant tokenExpiry;

    public synchronized String getAccessToken(WebClient tokenClient) {
        if (cachedToken != null && tokenExpiry != null && Instant.now().isBefore(tokenExpiry)) {
            return cachedToken;
        }

        String response = tokenClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", keycloakProperties.getRealm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials" +
                        "&client_id=" + keycloakProperties.getClientId() +
                        "&client_secret=" + keycloakProperties.getClientSecret())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (response == null) {
            throw new IllegalStateException("Failed to retrieve token from Keycloak");
        }

        JSONObject json = new JSONObject(response);
        cachedToken = json.getString("access_token");
        int expiresIn = json.getInt("expires_in");
        tokenExpiry = Instant.now().plusSeconds(expiresIn - 30);
        return cachedToken;
    }

}
