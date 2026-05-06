package com.voyagrr.storageservice.repository;

import com.voyagrr.storageservice.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query(value = """
            select id as groupId
            from groups
            where id = :groupId
            and owner_id = :ownerId
            limit 1
            """, nativeQuery = true)
    Long findByGroupIdAndOwnerId(@Param("groupId") long groupId, @Param("ownerId") String ownerId);

    @Query(value = """
            SELECT g.* FROM groups g 
            JOIN group_members gm ON g.id = gm.group_id 
            WHERE gm.user_id = :userId 
            AND g.name ILIKE %:query%
            """, nativeQuery = true)
    java.util.List<Group> searchGroupsByNameAndUserId(@Param("query") String query, @Param("userId") String userId);

}
