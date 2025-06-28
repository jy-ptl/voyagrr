package com.voyagrr.storageservice.service.impl;

import com.voyagrr.storageservice.repository.DirectoryRepository;
import com.voyagrr.storageservice.service.DirectoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectoryServiceImpl implements DirectoryService {

    private final DirectoryRepository directoryRepository;



}
