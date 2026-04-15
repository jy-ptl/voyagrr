package com.voyagrr.processingservice.service.grpc.server;

import com.voyagrr.common.proto.*;
import com.voyagrr.processingservice.service.ProcessingService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcProcessingServiceImpl extends ProcessingServiceGrpc.ProcessingServiceImplBase {

    private final ProcessingService processingService;

    @Override
    public void processFile(ProcessFileRequest request, StreamObserver<ProcessFileResponse> responseObserver) {
        responseObserver
                .onNext(ProcessFileResponse.newBuilder().setSuccess(processingService.processFile(request)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void processTrip(ProcessTripRequest request,
            StreamObserver<ProcessTripResponse> responseObserver) {

        String jobId = processingService.processTrip(
                request.getTripId(),
                request.getDirectoryId(),
                request.getGroupId(),
                request.getRequestedBy());

        responseObserver.onNext(
                ProcessTripResponse.newBuilder()
                        .setJobId(jobId)
                        .setStatus("QUEUED")
                        .build());

        responseObserver.onCompleted();
    }

}
