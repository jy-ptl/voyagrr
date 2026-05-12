package com.voyagrr.tripservice.service.grpc.client;

import org.springframework.stereotype.Component;

import java.util.List;

import com.voyagrr.common.proto.*;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Component
@RequiredArgsConstructor
public class StorageGrpcClient {

    @GrpcClient("storage-service")
    private StorageServiceGrpc.StorageServiceBlockingStub stub;

    public long createDirectoryForTrip(String keycloakUserId, String directoryName) {
        return stub.createDirectoryForTrip(CreateDirectoryForTripRequest.newBuilder().setKeycloakUserId(keycloakUserId)
                .setDirectoryName(directoryName).build()).getDirectoryId();
    }

    public long createOrValidateGroupForTrip(long groupId, List<String> keycloakUserIds, String ownerId) {
        return stub.createOrValidateGroupForTrip(CreateOrValidateGroupForTripRequest.newBuilder().setGroupId(groupId)
                .setOwnerId(ownerId).addAllKeycloakUserId(keycloakUserIds).build()).getGroupId();
    }

    public List<Long> getGroupIdsForUser(String keycloakUserId) {
        return stub.getGroupIdsForUser(GetGroupIdsForUserRequest.newBuilder()
                .setKeycloakUserId(keycloakUserId).build()).getGroupIdList();
    }

    public void addMediaShare(long directoryId, long groupId) {
        stub.addMediaShare(AddMediaShareRequest.newBuilder().setDirectoryId(directoryId).setGroupId(groupId).build());
    }

}
