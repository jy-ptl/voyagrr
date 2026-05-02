package com.voyagrr.storageservice.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.voyagrr.common.constant.ExceptionConstant;
import com.voyagrr.common.exception.AccessDeniedException;
import com.voyagrr.common.exception.EntityNotFoundException;
import com.voyagrr.storageservice.dto.GroupCreateRequest;
import com.voyagrr.storageservice.dto.GroupResponse;
import com.voyagrr.storageservice.dto.GroupUpdateRequest;
import com.voyagrr.storageservice.model.Group;
import com.voyagrr.storageservice.model.GroupMember;
import com.voyagrr.storageservice.model.GroupMemberId;
import com.voyagrr.storageservice.repository.GroupMemberRepository;
import com.voyagrr.storageservice.repository.GroupRepository;
import com.voyagrr.storageservice.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    public Long create(GroupCreateRequest request, String keycloakUserId) {
        return groupRepository
                .save(Group
                        .builder()
                        .name(request.name())
                        .ownerId(keycloakUserId)
                        .build())
                .getId();
    }

    @Override
    public Long createOrValidateGroupForTrip(long groupId, String groupName, String keycloakUserId,
            List<String> members) {
        if (groupId != 0)
            if (groupRepository.findByGroupIdAndOwnerId(groupId, keycloakUserId) != null)
                return groupId;
        Group group = groupRepository.save(Group.builder().name(groupName).ownerId(keycloakUserId).build());
        addGroupMembers(group, keycloakUserId, members);
        return group.getId();
    }

    private void addGroupMembers(Group group, String keycloakUserId, List<String> members) {

        Long groupId = group.getId();
        List<GroupMember> groupMembers = new ArrayList<>();

        groupMembers.add(GroupMember.builder().id(new GroupMemberId(keycloakUserId, groupId)).group(group).build());

        if (members != null)
            for (String userId : members) {
                if (userId.equals(keycloakUserId))
                    continue;
                groupMembers.add(GroupMember.builder().id(new GroupMemberId(userId, groupId)).group(group).build());
            }

        groupMemberRepository.saveAll(groupMembers);

    }

    @Override
    public List<String> findUserIdsByGroupId(long groupId) {
        return groupMemberRepository.findUserIdsByGroupId(groupId);
    }
    
    @Override
    public List<Long> findGroupIdsByUserId(String userId) {
        return groupMemberRepository.findGroupIdsByUserId(userId);
    }

    @Override
    public List<GroupResponse> getGroupsForUser(String keycloakUserId) {
        List<Long> groupIds = findGroupIdsByUserId(keycloakUserId);
        return groupRepository.findAllById(groupIds).stream()
                .map(group -> GroupResponse.builder()
                        .groupId(group.getId())
                        .name(group.getName())
                        .ownerId(group.getOwnerId())
                        .members(findUserIdsByGroupId(group.getId()))
                        .build())
                .toList();
    }

    @Override
    public GroupResponse getGroupById(long groupId, String keycloakUserId) {
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new EntityNotFoundException(ExceptionConstant.ENTITY_DOES_NOT_EXISTS
                        .formatted(ExceptionConstant.RESOURCES.GROUP)));

        List<String> members = findUserIdsByGroupId(groupId);
        if (!members.contains(keycloakUserId)) {
            throw new AccessDeniedException(ExceptionConstant.ACCESS_DENIED_FOR_RESOURCE
                    .formatted("VIEW", ExceptionConstant.RESOURCES.GROUP));
        }

        return GroupResponse.builder()
                .groupId(group.getId())
                .name(group.getName())
                .ownerId(group.getOwnerId())
                .members(members)
                .build();
    }

    @Override
    public void updateGroup(long groupId, GroupUpdateRequest request, String keycloakUserId) {
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new EntityNotFoundException(ExceptionConstant.ENTITY_DOES_NOT_EXISTS
                        .formatted(ExceptionConstant.RESOURCES.GROUP)));

        if (!group.getOwnerId().equals(keycloakUserId)) {
            throw new AccessDeniedException(ExceptionConstant.ACCESS_DENIED_FOR_RESOURCE
                    .formatted("UPDATE", ExceptionConstant.RESOURCES.GROUP));
        }

        if (request.name() != null) {
            group.setName(request.name());
        }

        if (request.members() != null) {
            groupMemberRepository.deleteByGroupId(groupId);
            addGroupMembers(group, keycloakUserId, request.members());
        }

        groupRepository.save(group);
    }

}
