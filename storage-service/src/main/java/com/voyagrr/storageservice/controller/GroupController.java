package com.voyagrr.storageservice.controller;

import com.voyagrr.storageservice.dto.GroupCreateRequest;
import com.voyagrr.storageservice.dto.GroupResponse;
import com.voyagrr.storageservice.dto.GroupUpdateRequest;
import com.voyagrr.storageservice.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Slf4j
@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
@Tag(name = "Group", description = "APIs for managing groups")
@SecurityRequirement(name = "bearerAuth")
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "Create a group", description = "Create a new group")
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public ResponseEntity<Long> create(@RequestBody GroupCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getSubject();
        return ResponseEntity.ok().body(groupService.create(request, keycloakUserId));
    }

    @Operation(summary = "Get groups for user", description = "Get all groups the authenticated user belongs to")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<GroupResponse>> getGroupsForUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(groupService.getGroupsForUser(jwt.getSubject()));
    }

    @Operation(summary = "Get group by id", description = "Get group details by group id")
    @RequestMapping(value = "{groupId}", method = RequestMethod.GET)
    public ResponseEntity<GroupResponse> getGroupById(@PathVariable(name = "groupId") long groupId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(groupService.getGroupById(groupId, jwt.getSubject()));
    }

    @Operation(summary = "Update group", description = "Update group details (only by owner)")
    @RequestMapping(value = "{groupId}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateGroup(@PathVariable(name = "groupId") long groupId,
            @RequestBody GroupUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        groupService.updateGroup(groupId, request, jwt.getSubject());
        return ResponseEntity.ok().body("Group updated successfully");
    }

}
