package com.voyagrr.tripservice.service.grpc.client;

import org.springframework.stereotype.Component;

import java.util.List;

import com.voyagrr.common.proto.*;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Component
@RequiredArgsConstructor
public class ProcessingGrpcClient {

    @GrpcClient("processing-service")
    private ProcessingServiceGrpc.ProcessingServiceBlockingStub stub;

    public String processTrip(Long tripId, Long directoryId, Long groupId, String keycloakUserId) {
        ProcessTripResponse response = stub.processTrip(
                ProcessTripRequest.newBuilder()
                        .setTripId(tripId)
                        .setGroupId(groupId)
                        .setDirectoryId(directoryId)
                        .setRequestedBy(keycloakUserId)
                        .build());
        return response.getJobId();
    }

}
