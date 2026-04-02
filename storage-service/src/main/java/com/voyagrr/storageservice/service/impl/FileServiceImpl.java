package com.voyagrr.storageservice.service.impl;

import com.voyagrr.common.constant.ExceptionConstant;
import com.voyagrr.common.enumeration.FileStatus;
import com.voyagrr.common.exception.EntityNotFoundException;
import com.voyagrr.storageservice.enumeration.EncodingStatus;
import com.voyagrr.storageservice.model.Directory;
import com.voyagrr.storageservice.model.File;
import com.voyagrr.storageservice.repository.FileRepository;
import com.voyagrr.storageservice.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.voyagrr.common.constant.ExceptionConstant.ENTITY_DOES_NOT_EXISTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Override
    public File findById(long fileId) {
        return fileRepository.findById(fileId).orElseThrow(
                () -> new EntityNotFoundException(ENTITY_DOES_NOT_EXISTS.formatted(ExceptionConstant.RESOURCES.FILE)));
    }

    @Override
    public List<File> findByDirectory(Directory directory) {
        return fileRepository.findByDirectory(directory);
    }

    @Override
    public void deleteAll(List<File> files) {
        fileRepository.deleteAll(files);
    }

    @Override
    public boolean updateFileStatus(long fileId, String status) {
        File file = fileRepository.findById(fileId).orElseThrow(
                () -> new EntityNotFoundException(ENTITY_DOES_NOT_EXISTS.formatted(ExceptionConstant.RESOURCES.FILE)));
        file.setFileStatus(FileStatus.valueOf(status));
        fileRepository.save(file);
        return true;
    }
}
