package com.voyagrr.userservice.service;

import com.voyagrr.userservice.dto.UserResponse;
import com.voyagrr.userservice.dto.UserUpdateRequest;
import com.voyagrr.userservice.model.User;

public interface UserService {

    User save(User user);

    UserResponse getUserResponseByKeycloakUserId(String keycloakUserId);

    UserResponse updateUserInfo(UserUpdateRequest userUpdateRequest, String keycloakUserId);

}
