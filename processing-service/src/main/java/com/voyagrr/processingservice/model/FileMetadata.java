package com.voyagrr.processingservice.model;

import com.voyagrr.processingservice.config.Auditable;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file_metadata")
public class FileMetadata extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_id", nullable = false, updatable = false)
    private Long fileId;

    @Column(name = "minio_object_key", nullable = false, updatable = false)
    private String minioObjectKey;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

}
