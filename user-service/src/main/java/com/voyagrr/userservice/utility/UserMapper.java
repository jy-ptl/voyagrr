package com.voyagrr.userservice.utility;

import com.voyagrr.userservice.dto.UserCreateRequest;
import com.voyagrr.userservice.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User userCreateRequestToUser(UserCreateRequest request) {
        return User.builder()
                .username(request.username())
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .build();
    }

}
