package com.voyagrr.userservice.service;

import com.voyagrr.userservice.dto.UserCreateRequest;
import com.voyagrr.userservice.dto.UserLoginRequest;

public interface AuthenticationService {
    String register(UserCreateRequest request);

    String login(UserLoginRequest request);

    String refreshToken(String refreshToken);
}
