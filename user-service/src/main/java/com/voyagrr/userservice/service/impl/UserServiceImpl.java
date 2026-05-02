package com.voyagrr.userservice.service.impl;

import com.voyagrr.common.constant.ExceptionConstant;
import com.voyagrr.common.exception.EntityNotFoundException;
import com.voyagrr.common.exception.KeycloakAuthException;
import com.voyagrr.userservice.config.keycloak.KeycloakProperties;
import com.voyagrr.userservice.dto.UserResponse;
import com.voyagrr.userservice.dto.UserSearchResponse;
import com.voyagrr.userservice.dto.UserUpdateRequest;
import com.voyagrr.userservice.model.User;
import com.voyagrr.userservice.repository.UserRepository;
import com.voyagrr.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import static com.voyagrr.common.constant.ExceptionConstant.ENTITY_DOES_NOT_EXISTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Qualifier(value = "keycloakWebClient")
    private final WebClient keycloakWebClient;

    @Qualifier(value = "keycloakAuthWebClient")
    private final WebClient keycloakAuthWebClient;

    private final KeycloakProperties keycloakProperties;

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserResponse getUserResponseByKeycloakUserId(String keycloakUserId) {
        return userRepository.getUserResponseByKeycloakUserId(keycloakUserId);
    }

    @Override
    public UserResponse updateUserInfo(UserUpdateRequest userUpdateRequest, String keycloakUserId) {
        User user = userRepository.getUserByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new EntityNotFoundException(
                        ENTITY_DOES_NOT_EXISTS.formatted(ExceptionConstant.RESOURCES.USER)));

        JSONObject payload = new JSONObject()
                .put("firstName", userUpdateRequest.firstName())
                .put("lastName", userUpdateRequest.lastName());

        keycloakAuthWebClient.put()
                .uri("/admin/realms/{realm}/users/{id}", keycloakProperties.getRealm(), keycloakUserId)
                .bodyValue(payload.toString())
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class).flatMap(errorBody -> {
                    HttpStatusCode status = response.statusCode();
                    if (status == HttpStatus.NOT_FOUND) {
                        return Mono.error(new EntityNotFoundException("user not found in keycloak"));
                    }
                    return Mono.error(new KeycloakAuthException("update failed : " + errorBody));
                })).toBodilessEntity().block();

        user.setFirstName(userUpdateRequest.firstName());
        user.setLastName(userUpdateRequest.lastName());
        userRepository.save(user);

        return userRepository.getUserResponseByKeycloakUserId(keycloakUserId);
    }

    @Override
    public List<UserSearchResponse> searchUsers(String query) {
        return userRepository.searchUsers(query);
    }

}
