package com.voyagrr.storageservice.repository;

import com.voyagrr.storageservice.model.GroupMember;
import com.voyagrr.storageservice.model.GroupMemberId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
}
