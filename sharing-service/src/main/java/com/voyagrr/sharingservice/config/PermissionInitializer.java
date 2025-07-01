package com.voyagrr.sharingservice.config;

import com.voyagrr.sharingservice.model.Permission;
import com.voyagrr.sharingservice.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class PermissionInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) throws Exception {
        Arrays.stream(com.voyagrr.sharingservice.enumeration.Permission.values()).forEach(permissionName -> {
            boolean exists = permissionRepository.existsByName(permissionName);
            if (!exists) {
                permissionRepository.save(Permission.builder()
                        .name(permissionName)
                        .build());
            }
        });
    }
}
