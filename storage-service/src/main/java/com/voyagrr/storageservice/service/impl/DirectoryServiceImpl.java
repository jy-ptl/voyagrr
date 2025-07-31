package com.voyagrr.storageservice.service.impl;

import com.voyagrr.common.enumeration.Permission;
import com.voyagrr.common.exception.AccessDeniedException;
import com.voyagrr.common.exception.EntityNotFoundException;
import com.voyagrr.common.proto.*;
import com.voyagrr.storageservice.dto.*;
import com.voyagrr.storageservice.model.Directory;
import com.voyagrr.storageservice.model.File;
import com.voyagrr.storageservice.repository.DirectoryRepository;
import com.voyagrr.storageservice.service.DirectoryService;
import com.voyagrr.storageservice.service.FileService;
import com.voyagrr.storageservice.service.StorageService;
import com.voyagrr.storageservice.service.grpc.SharingGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.voyagrr.common.constant.ExceptionConstant.*;
import static com.voyagrr.common.proto.PermissionDeletionType.DIRECTORY;
import static com.voyagrr.common.proto.PermissionDeletionType.FILE;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectoryServiceImpl implements DirectoryService {

    private final DirectoryRepository directoryRepository;
    private final SharingGrpcClient sharingGrpcClient;
    private final FileService fileService;
    private final StorageService storageService;

    @Override
    public Directory findDirectoryById(Long directoryId) {
        return directoryRepository.findById(directoryId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_DOES_NOT_EXISTS.formatted(RESOURCES.DIRECTORY)));
    }

    @Override
    public Long create(DirectoryCreateRequest request, String keycloakUserId) {
        Directory directory = new Directory();
        if (request.parentDirectoryId() != null) {
            Directory parentDirectory = findDirectoryById(request.parentDirectoryId());
            directory.setParentDirectory(parentDirectory);
        }
        directory.setName(request.name());
        directory.setOwnerId(keycloakUserId);
        Long directoryId = directoryRepository.save(directory).getId();
        boolean defaultPermissionsGenerated = sharingGrpcClient.createDefaultPermissions(keycloakUserId, directoryId);
        if (!defaultPermissionsGenerated)
            log.error("Unable to create default permissions for directory with id {}", directoryId);
        return directoryId;
    }

    @Override
    public String buildMinioObjectPathFromDirectoryId(Long directoryId) {
        return directoryRepository.buildMinioObjectPathFromDirectoryId(directoryId);
    }

    @Override
    public List<DirectoryTreeResponse> getAllDirectoriesOfUser(String keycloakUserId) {
        return buildDirectoryTree(directoryRepository.getAllDirectoriesRecursivelyForUserId(keycloakUserId));
    }

    @Override
    @Transactional
    public String deleteDirectoryById(Long directoryId, String keycloakUserId) {
        Directory directory = findDirectoryById(directoryId);

        boolean allowed = sharingGrpcClient.hasPermissionForDirectories(
                keycloakUserId, directoryRepository.getAllAncestorsIncludingSelf(directoryId).stream()
                        .mapToLong(DirectoryFlatResponse::id).boxed().toList(),
                Permission.DELETE.name());

        if (!allowed)
            throw new AccessDeniedException(
                    ACCESS_DENIED_FOR_RESOURCE.formatted(Permission.DELETE.name(), RESOURCES.DIRECTORY));

        deleteRecursively(directory);

        return "Success";
    }

    @Override
    public DirectoryContentResponse getDirectoryContents(Long directoryId, String keycloakUserId) {

        Directory directory = findDirectoryById(directoryId);

        List<File> files = fileService.findByDirectory(directory);
        List<Directory> directories = directoryRepository.findByParentDirectory(directory);

        ContentAccessResponse accessResponse = sharingGrpcClient
                .contentAccessOfDirectory(ContentAccessRequest.newBuilder()
                        .setUserId(keycloakUserId)
                        .addAllDirectoryId(directoryRepository.getAllAncestorsIncludingSelf(directoryId).stream()
                                .mapToLong(DirectoryFlatResponse::id).boxed().toList())
                        .addAllChildDirectoryId(directories.stream().mapToLong(Directory::getId).boxed().toList())
                        .addAllFileId(files.stream().mapToLong(File::getId).boxed().toList())
                        .build());

        Map<Long, List<String>> filePermissionsMap = accessResponse.getFilesList().stream()
                .collect(Collectors.toMap(
                        FileAccessResponse::getFileId,
                        FileAccessResponse::getPermissionList));

        List<String> rootDirPermissions = accessResponse.getRootDirectoryPermissionsList();

        Map<Long, List<String>> dirPermissionsMap = accessResponse.getDirectoriesList().stream()
                .collect(Collectors.toMap(
                        DirectoryAccessResponse::getDirectoryId,
                        DirectoryAccessResponse::getPermissionList));

        List<FileResponse> fileResponses = files.stream()
                .map(f -> {
                    List<String> filePermissions = filePermissionsMap.getOrDefault(f.getId(), new ArrayList<>());

                    List<String> combinedPermissions = Stream.concat(
                            filePermissions.stream(),
                            rootDirPermissions.stream())
                            .distinct()
                            .toList();

                    return new AbstractMap.SimpleEntry<>(f, combinedPermissions);
                })
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> new FileResponse(
                        entry.getKey().getId(),
                        entry.getKey().getName(),
                        entry.getKey().getMimeType(),
                        entry.getValue()))
                .toList();

        return DirectoryContentResponse.builder()
                .permission(rootDirPermissions)
                .files(fileResponses)
                .children(directories.stream()
                        .filter(f -> dirPermissionsMap.containsKey(f.getId()))
                        .map(f -> new DirectoryResponse(
                                f.getId(),
                                f.getName(),
                                dirPermissionsMap.get(f.getId())))
                        .toList())
                .build();
    }

    @Override
    public List<Long> getAllAncestorsIncludingSelfFromFileId(long fileId) {
        File file = fileService.findById(fileId);
        return directoryRepository.getAllAncestorsIncludingSelf(file.getDirectory().getId()).stream()
                .mapToLong(DirectoryFlatResponse::id).boxed().toList();
    }

    private List<DirectoryTreeResponse> buildDirectoryTree(List<DirectoryFlatResponse> flatList) {
        Map<Long, DirectoryTreeResponse> map = new HashMap<>();
        List<DirectoryTreeResponse> roots = new ArrayList<>();

        for (DirectoryFlatResponse flat : flatList) {
            map.put(flat.id(), new DirectoryTreeResponse(flat.id(), flat.name()));
        }

        for (DirectoryFlatResponse flat : flatList) {
            DirectoryTreeResponse node = map.get(flat.id());
            if (flat.parentDirectoryId() == null) {
                roots.add(node);
            } else {
                DirectoryTreeResponse parent = map.get(flat.parentDirectoryId());
                if (parent != null) {
                    parent.addChild(node);
                }
            }
        }
        return roots;
    }

    private void deleteRecursively(Directory directory) {

        List<Long> allDirectoryIds = new ArrayList<>();
        List<File> allFiles = new ArrayList<>();

        collectDirectoriesAndFiles(directory, allDirectoryIds, allFiles);

        storageService.deleteFiles(allFiles);
        fileService.deleteAll(allFiles);
        directoryRepository.deleteAllById(allDirectoryIds);

        List<DeletePermissionDto> permissionsForDirectories = allDirectoryIds.stream()
                .map(id -> DeletePermissionDto.newBuilder()
                        .setDirectoryId(id)
                        .build())
                .toList();

        List<DeletePermissionDto> permissionsForFiles = allFiles.stream()
                .map(file -> DeletePermissionDto.newBuilder()
                        .setFileId(file.getId())
                        .build())
                .toList();

        DeletePermissionRequest directoriesPermissionsDeletionRequest = DeletePermissionRequest.newBuilder()
                .addAllDeletePermission(permissionsForDirectories)
                .setType(DIRECTORY)
                .build();

        DeletePermissionRequest filesPermissionsDeletionRequest = DeletePermissionRequest.newBuilder()
                .addAllDeletePermission(permissionsForFiles)
                .setType(FILE).build();

        boolean successForDirectories = sharingGrpcClient.deletePermission(directoriesPermissionsDeletionRequest);
        boolean successForFiles = sharingGrpcClient.deletePermission(filesPermissionsDeletionRequest);

        if (!successForDirectories)
            log.info("Failed to delete permissions for directories %s".formatted(allDirectoryIds.toString()));

        if (!successForFiles)
            log.info("Failed to delete permissions for files %s"
                    .formatted(Arrays.toString(allFiles.stream().mapToLong(File::getId).toArray())));

    }

    private void collectDirectoriesAndFiles(Directory directory, List<Long> directoryIds, List<File> allFiles) {
        directoryIds.add(directory.getId());

        List<File> files = fileService.findByDirectory(directory);
        allFiles.addAll(files);

        List<Directory> children = directoryRepository.findByParentDirectory(directory);
        for (Directory child : children) {
            collectDirectoriesAndFiles(child, directoryIds, allFiles);
        }
    }
}
