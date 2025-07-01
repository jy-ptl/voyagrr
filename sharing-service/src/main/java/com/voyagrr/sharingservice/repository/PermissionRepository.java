package com.voyagrr.sharingservice.repository;

import com.voyagrr.sharingservice.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByName(com.voyagrr.sharingservice.enumeration.Permission name);

    Optional<Permission> findByName(com.voyagrr.sharingservice.enumeration.Permission name);
}
