package com.voyagrr.sharingservice.service;

import com.voyagrr.sharingservice.dto.GroupCreateRequest;

public interface GroupService {

    Long create(GroupCreateRequest request, String keycloakUserId);

}
