package com.voyagrr.storageservice.service.impl;

import com.voyagrr.storageservice.repository.FileRepository;
import com.voyagrr.storageservice.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

}
