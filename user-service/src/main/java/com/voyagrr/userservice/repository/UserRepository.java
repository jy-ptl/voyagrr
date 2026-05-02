package com.voyagrr.userservice.repository;

import java.util.List;
import java.util.Optional;

import com.voyagrr.userservice.dto.UserResponse;
import com.voyagrr.userservice.dto.UserSearchResponse;
import com.voyagrr.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = """
            select username as username, first_name as firstName, last_name as lastName, email as email
                from users where keycloak_user_id = :keycloakUserId and is_deleted = false
                 """, nativeQuery = true)
    UserResponse getUserResponseByKeycloakUserId(@Param("keycloakUserId") String keycloakUserId);

    @Query(value = """
            select keycloak_user_id as keycloakUserId, username as username, first_name as firstName, last_name as lastName, email as email
                from users 
                where (username ilike %:query% 
                    or email ilike %:query% 
                    or first_name ilike %:query% 
                    or last_name ilike %:query%)
                and is_deleted = false
            """, nativeQuery = true)
    List<UserSearchResponse> searchUsers(@Param("query") String query);

    Optional<User> getUserByKeycloakUserId(String keycloakUserId);
}
