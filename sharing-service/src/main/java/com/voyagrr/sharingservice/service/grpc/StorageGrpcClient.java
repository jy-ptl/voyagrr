package com.voyagrr.sharingservice.service.grpc;

import com.voyagrr.common.proto.*;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StorageGrpcClient {

    @GrpcClient("storage-service")
    private StorageServiceGrpc.StorageServiceBlockingStub stub;

    public List<Long> getAllAncestorsIncludingSelf(long directoryId) {
        return stub.getAllAncestorsIncludingSelf(AncestorsIncludingSelfRequest.newBuilder()
                .setDirectoryId(directoryId)
                .build()).getDirectoryIdList();
    }

    public List<Long> getAllAncestorsIncludingSelfForFileId(long fileId) {
        return stub.getAllAncestorsIncludingSelfFromFileId(
                AncestorsIncludingSelfRequestForFile.newBuilder()
                        .setFileId(fileId)
                        .build()).getDirectoryIdList();
    }


}
