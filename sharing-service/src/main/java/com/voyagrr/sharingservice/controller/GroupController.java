package com.voyagrr.sharingservice.controller;

import com.voyagrr.sharingservice.dto.GroupCreateRequest;
import com.voyagrr.sharingservice.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public ResponseEntity<Long> create(@RequestBody GroupCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getSubject();
        return ResponseEntity.ok().body(groupService.create(request, keycloakUserId));
    }

}
