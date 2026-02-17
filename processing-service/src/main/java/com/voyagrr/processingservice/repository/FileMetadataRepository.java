package com.voyagrr.processingservice.repository;

import com.voyagrr.processingservice.model.FileMetadata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

}
