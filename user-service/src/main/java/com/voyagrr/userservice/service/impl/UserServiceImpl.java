package com.voyagrr.userservice.service.impl;

import com.voyagrr.userservice.model.User;
import com.voyagrr.userservice.repository.UserRepository;
import com.voyagrr.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

}
