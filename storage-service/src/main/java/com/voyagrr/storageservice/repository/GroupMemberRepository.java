package com.voyagrr.storageservice.repository;

import java.util.List;

import com.voyagrr.storageservice.model.GroupMember;
import com.voyagrr.storageservice.model.GroupMemberId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

    @Query(value = "SELECT user_id FROM group_members WHERE group_id = :groupId", nativeQuery = true)
    List<String> findUserIdsByGroupId(@Param("groupId") Long groupId);

}
