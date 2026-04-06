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

}
