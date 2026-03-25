package com.voyagrr.storageservice.service;

import com.voyagrr.storageservice.dto.GroupCreateRequest;

public interface GroupService {

    Long create(GroupCreateRequest request, String keycloakUserId);

}
