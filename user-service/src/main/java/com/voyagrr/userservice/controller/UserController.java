package com.voyagrr.userservice.controller;

import com.voyagrr.userservice.dto.UserResponse;
import com.voyagrr.userservice.dto.UserSearchResponse;
import com.voyagrr.userservice.dto.UserUpdateRequest;
import com.voyagrr.userservice.service.UserService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "APIs related to users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(summary = "User info", description = "Get user info of authenticated user")
    @RequestMapping(value = "info", method = RequestMethod.GET)
    public ResponseEntity<UserResponse> details(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(userService.getUserResponseByKeycloakUserId(jwt.getSubject()));
    }

    @Operation(summary = "Update user info", description = "Update user info")
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ResponseEntity<UserResponse> update(@Valid @RequestBody UserUpdateRequest userUpdateRequest,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(userService.updateUserInfo(userUpdateRequest, jwt.getSubject()));
    }

    @Operation(summary = "Search users", description = "Search users by username, email, first name, or last name")
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public ResponseEntity<List<UserSearchResponse>> search(@RequestParam("query") String query) {
        return ResponseEntity.ok().body(userService.searchUsers(query));
    }

}
