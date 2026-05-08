package com.voyagrr.processingservice.service.grpc.client;

import com.voyagrr.common.proto.*;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserGrpcClient {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub stub;

    public Map<String, UserInfo> getUsersInfo(List<String> userIds) {
        GetUsersInfoResponse response = stub.getUsersInfo(GetUsersInfoRequest.newBuilder().addAllKeycloakUserId(userIds).build());
        return response.getUsersList().stream()
                .collect(Collectors.toMap(UserInfo::getKeycloakUserId, u -> u));
    }
}
