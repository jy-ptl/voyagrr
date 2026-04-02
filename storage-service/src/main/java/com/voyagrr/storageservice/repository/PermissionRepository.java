package com.voyagrr.storageservice.repository;

import com.voyagrr.storageservice.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByName(com.voyagrr.common.enumeration.Permission name);

    Optional<Permission> findByName(com.voyagrr.common.enumeration.Permission name);
}
