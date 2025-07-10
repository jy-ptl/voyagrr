package com.voyagrr.storageservice.service.impl;

import com.voyagrr.storageservice.model.Directory;
import com.voyagrr.storageservice.model.File;
import com.voyagrr.storageservice.repository.FileRepository;
import com.voyagrr.storageservice.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Override
    public List<File> findByDirectory(Directory directory) {
        return fileRepository.findByDirectory(directory);
    }

    @Override
    public void deleteAll(List<File> files) {
        fileRepository.deleteAll(files);
    }
}
