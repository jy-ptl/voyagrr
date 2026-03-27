package com.voyagrr.processingservice.repository;

import java.util.List;
import java.util.Optional;

import com.voyagrr.processingservice.model.FileMetadata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    Optional<FileMetadata> findByFileId(long fileId);

    @Query("SELECT f FROM FileMetadata f WHERE f.fileId = :fileId")
    List<FileMetadata> getFileMetadataByFileId(@Param("fileId") Long fileId);

    @Query("SELECT f FROM FileMetadata f WHERE f.fileId IN (:fileIds)")
    List<FileMetadata> getFileMetadataByFileIds(@Param("fileIds") List<Long> fileIds);
}
