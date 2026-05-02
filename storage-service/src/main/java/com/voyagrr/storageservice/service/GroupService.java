package com.voyagrr.storageservice.service;

import java.util.List;

import com.voyagrr.storageservice.dto.GroupCreateRequest;
import com.voyagrr.storageservice.dto.GroupResponse;
import com.voyagrr.storageservice.dto.GroupUpdateRequest;

public interface GroupService {

    Long create(GroupCreateRequest request, String keycloakUserId);

    Long createOrValidateGroupForTrip(long groupId, String groupName, String keycloakUserId, List<String> members);

    List<String> findUserIdsByGroupId(long groupId);

    List<Long> findGroupIdsByUserId(String userId);

    List<GroupResponse> getGroupsForUser(String keycloakUserId);

    GroupResponse getGroupById(long groupId, String keycloakUserId);

    void updateGroup(long groupId, GroupUpdateRequest request, String keycloakUserId);

}
