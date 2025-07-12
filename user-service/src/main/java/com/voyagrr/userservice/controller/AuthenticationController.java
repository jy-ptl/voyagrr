package com.voyagrr.userservice.controller;

import com.voyagrr.userservice.dto.UserCreateRequest;
import com.voyagrr.userservice.dto.UserLoginRequest;
import com.voyagrr.userservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public ResponseEntity<String> register(@RequestBody UserCreateRequest request) {
        return ResponseEntity.ok().body(authenticationService.register(request));
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestBody UserLoginRequest request) {
        return ResponseEntity.ok().body(authenticationService.login(request));
    }


}
