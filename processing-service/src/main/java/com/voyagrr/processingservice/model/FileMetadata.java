package com.voyagrr.processingservice.model;

import java.util.Map;

import com.voyagrr.processingservice.config.entity.Auditable;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

}
