package com.voyagrr.userservice.service.impl;

import com.voyagrr.userservice.config.KeycloakProperties;
import com.voyagrr.userservice.dto.UserCreateRequest;
import com.voyagrr.userservice.dto.UserLoginRequest;
import com.voyagrr.userservice.model.User;
import com.voyagrr.userservice.service.AuthenticationService;
import com.voyagrr.userservice.service.UserService;
import com.voyagrr.userservice.utility.UserMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class KeycloakAuthenticationService implements AuthenticationService {

    @Qualifier(value = "keycloakWebClient")
    private final WebClient keycloakWebClient;

    @Qualifier(value = "keycloakAuthWebClient")
    private final WebClient keycloakAuthWebClient;

    private final KeycloakProperties keycloakProperties;

    private final UserService userService;

    private final UserMapper userMapper;

    @Override
    public String register(UserCreateRequest request) {

        JSONObject payload = new JSONObject()
                .put("username", request.username())
                .put("enabled", true)
                .put("email", request.email())
                .put("firstName", request.firstName())
                .put("lastName", request.lastName())
                .put("credentials", new org.json.JSONArray()
                        .put(new JSONObject()
                                .put("type", "password")
                                .put("value", request.password())
                                .put("temporary", false)));

        return keycloakAuthWebClient.post()
                .uri("/admin/realms/{realm}/users", keycloakProperties.getRealm())
                .bodyValue(payload.toString())
                .retrieve()
                .toBodilessEntity()
                .map(response -> {
                    URI location = response.getHeaders().getLocation();
                    if (location != null) {
                        String[] parts = location.getPath().split("/");
                        String keycloakUserId = parts[parts.length - 1];

                        User user = userMapper.userCreateRequestToUser(request);
                        user.setKeycloakUserId(keycloakUserId);
                        userService.save(user);

                        return keycloakUserId;
                    }
                    return "Created but no user ID found";
                })
                .onErrorResume(error -> Mono.just("Error: " + error.getMessage()))
                .block();
    }

    @Override
    public String login(UserLoginRequest request) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", request.username());
        formData.add("password", request.password());
        formData.add("grant_type", "password");
        formData.add("scope", "openid profile email");
        formData.add("client_id", keycloakProperties.getClientId());
        formData.add("client_secret", keycloakProperties.getClientSecret());

        return keycloakWebClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", keycloakProperties.getRealm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class).flatMap(error -> Mono.error(new RuntimeException("Login failed: " + error)))
                )
                .bodyToMono(String.class)
                .block();
    }

}
