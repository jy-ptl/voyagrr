package com.voyagrr.sharingservice.service.impl;

import com.voyagrr.sharingservice.dto.GroupCreateRequest;
import com.voyagrr.sharingservice.model.Group;
import com.voyagrr.sharingservice.repository.GroupRepository;
import com.voyagrr.sharingservice.service.GroupService;
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
