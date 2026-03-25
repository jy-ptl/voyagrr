package com.voyagrr.storageservice.service;

import com.voyagrr.storageservice.model.Directory;
import com.voyagrr.storageservice.model.File;

import java.util.List;

public interface FileService {

    File findById(long fileId);

    List<File> findByDirectory(Directory directory);

    void deleteAll(List<File> files);

    boolean updateFileStatus(long fileId, String status);
}
