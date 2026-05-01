package com.voyagrr.storageservice.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.voyagrr.storageservice.dto.GroupCreateRequest;
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

}
