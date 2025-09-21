package com.voyagrr.userservice.controller;

import com.voyagrr.userservice.dto.UserCreateRequest;
import com.voyagrr.userservice.dto.UserLoginRequest;
import com.voyagrr.userservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "APIs for user registration, login & authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Register a user", description = "Registering a user")
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ResponseEntity<String> register(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok().body(authenticationService.register(request));
    }

    @Operation(summary = "Login a user", description = "Login a user to retrive a token")
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginRequest request) {
        return ResponseEntity.ok().body(authenticationService.login(request));
    }

}
