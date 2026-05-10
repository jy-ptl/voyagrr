package com.voyagrr.userservice.service.grpc.server;

import com.voyagrr.common.proto.*;
import com.voyagrr.userservice.service.UserService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class GrpcUserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    @Override
    public void getUsersInfo(GetUsersInfoRequest request, StreamObserver<GetUsersInfoResponse> responseObserver) {
        List<UserInfo> users = request.getKeycloakUserIdList().stream()
                .map(id -> {
                    var user = userService.getUserResponseByKeycloakUserId(id);
                    return UserInfo.newBuilder()
                            .setKeycloakUserId(id)
                            .setUsername(user.username())
                            .setFirstName(user.firstName())
                            .setLastName(user.lastName())
                            .setEmail(user.email())
                            .build();
                })
                .toList();

        responseObserver.onNext(GetUsersInfoResponse.newBuilder().addAllUsers(users).build());
        responseObserver.onCompleted();
    }
}
