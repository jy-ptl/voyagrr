package com.voyagrr.storageservice.service;

import java.util.List;

import com.voyagrr.storageservice.dto.GroupCreateRequest;

public interface GroupService {

    Long create(GroupCreateRequest request, String keycloakUserId);

    Long createOrValidateGroupForTrip(long groupId, String groupName, String keycloakUserId, List<String> members);

}
