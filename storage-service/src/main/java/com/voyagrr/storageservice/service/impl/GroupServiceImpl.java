package com.voyagrr.storageservice.service.impl;

import com.voyagrr.storageservice.dto.GroupCreateRequest;
import com.voyagrr.storageservice.model.Group;
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
}
