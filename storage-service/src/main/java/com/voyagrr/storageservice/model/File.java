package com.voyagrr.storageservice.model;

import com.voyagrr.storageservice.config.Auditable;
import com.voyagrr.storageservice.enumeration.EncodingStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class File extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String ownerId;

    @ManyToOne
    @JoinColumn(name = "directory_id", nullable = false)
    private Directory directory;

    @Column(name = "minio_object_key")
    private String minioObjectKey;

    @Column(name = "mime_type")
    private String mimeType;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "encoding_status", nullable = false)
    private EncodingStatus encodingStatus = EncodingStatus.PENDING;

}
