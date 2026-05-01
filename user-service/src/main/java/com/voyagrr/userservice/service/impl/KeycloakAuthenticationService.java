package com.voyagrr.userservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voyagrr.common.exception.KeycloakAuthException;
import com.voyagrr.common.exception.InvalidCredentialsException;
import com.voyagrr.common.exception.EntityAlreadyExistsException;
import com.voyagrr.userservice.config.keycloak.KeycloakProperties;
import com.voyagrr.userservice.dto.UserCreateRequest;
import com.voyagrr.userservice.dto.UserLoginRequest;
import com.voyagrr.userservice.model.User;
import com.voyagrr.userservice.service.AuthenticationService;
import com.voyagrr.userservice.service.UserService;
import com.voyagrr.userservice.service.grpc.client.StorageGrpcClient;
import com.voyagrr.userservice.utility.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
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

    private final StorageGrpcClient storageGrpcClient;

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
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class).flatMap(errorBody -> {
                    HttpStatusCode status = response.statusCode();
                    if (status == HttpStatus.CONFLICT) {
                        return Mono.error(new EntityAlreadyExistsException("user already exists"));
                    }
                    return Mono.error(new KeycloakAuthException());
                }))
                .toBodilessEntity()
                .map(response -> {
                    URI location = response.getHeaders().getLocation();
                    if (location != null) {
                        String[] parts = location.getPath().split("/");
                        String keycloakUserId = parts[parts.length - 1];

                        User user = userMapper.userCreateRequestToUser(request);
                        user.setKeycloakUserId(keycloakUserId);
                        userService.save(user);
                        storageGrpcClient.createDefaultSampleDirectoryForUser(keycloakUserId);
                        return keycloakUserId;
                    }
                    throw new KeycloakAuthException();
                })
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
                .onStatus(HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    try {
                                        ObjectMapper mapper = new ObjectMapper();
                                        JsonNode errorJson = mapper.readTree(errorBody);
                                        String error = errorJson.path("error").asText();
                                        String description = errorJson.path("error_description").asText();
                                        if ("invalid_grant".equals(error)) {
                                            return Mono.error(
                                                    new InvalidCredentialsException("Invalid username or password"));
                                        }
                                        return Mono.error(new KeycloakAuthException("login failed: " + description));
                                    } catch (Exception exception) {
                                        return Mono.error(new KeycloakAuthException("login failed: " + errorBody));
                                    }
                                }))
                .bodyToMono(String.class)
                .block();
    }

}
