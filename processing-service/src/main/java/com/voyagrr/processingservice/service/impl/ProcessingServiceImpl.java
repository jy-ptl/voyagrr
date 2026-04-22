package com.voyagrr.processingservice.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.voyagrr.common.enumeration.FileStatus;
import com.voyagrr.common.proto.*;
import com.voyagrr.processingservice.dto.FileProcessingEvent;
import com.voyagrr.processingservice.dto.GroupMember;
import com.voyagrr.processingservice.dto.ImageEmbeddingEvent;
import com.voyagrr.processingservice.dto.TripAnalyzeEvent;
import com.voyagrr.processingservice.service.ProcessingService;
import com.voyagrr.processingservice.service.grpc.client.StorageGrpcClient;
import com.voyagrr.processingservice.service.kafka.producer.FileMetadataEventProducer;
import com.voyagrr.processingservice.service.kafka.producer.ImageEmbeddingEventProducer;
import com.voyagrr.processingservice.service.kafka.producer.TripFacialRecognitionEventProducer;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessingServiceImpl implements ProcessingService {

    private final FileMetadataEventProducer fileMetadataEventProducer;
    private final StorageGrpcClient storageGrpcClient;

    private final TripFacialRecognitionEventProducer tripFacialRecognitionEventProducer;
    private final ImageEmbeddingEventProducer imageEmbeddingEventProducer;

    @Override
    public boolean processFile(ProcessFileRequest request) {
        fileMetadataEventProducer.sendFileUploadedEvent(FileProcessingEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .fileId(String.valueOf(request.getFileId()))
                .minioObjectKey(request.getMinioObjectKey())
                .timestamp(Instant.now())
                .build());
        storageGrpcClient.updateFileProcessingStatus(request.getFileId(), FileStatus.IN_METADATA_PROCESS.name());
        return true;
    }

    @Override
    public String processTrip(Long tripId, Long directoryId, Long groupId, String requestedBy) {
        String jobId = UUID.randomUUID().toString();

        GetTripProcessingDataResponse response = storageGrpcClient.getTripData(tripId, groupId);

        List<GroupMember> members = response.getGroupMembersList()
                .stream()
                .map(g -> GroupMember.builder()
                        .keycloakUserId(g.getKeycloakUserId())
                        .sampleDirectory(g.getSampleDirectory())
                        .build())
                .toList();

        TripAnalyzeEvent event = TripAnalyzeEvent.builder()
                .tripId(tripId)
                .tripDirectory("dir_" + directoryId)
                .groupMembers(members)
                .build();

        tripFacialRecognitionEventProducer.sendTripAnalysisEvent(event);

        return jobId;
    }

    @Override
    public boolean embeddSampleImages(String keycloakUserId, String sampleDirectory) {
        imageEmbeddingEventProducer.sendImageEmbeddingEvent(
                ImageEmbeddingEvent.builder().keycloakUserId(keycloakUserId).sampleDirectory(sampleDirectory).build());
        return true;
    }

}
