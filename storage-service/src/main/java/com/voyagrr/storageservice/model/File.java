package com.voyagrr.storageservice.model;

import com.voyagrr.storageservice.config.Auditable;
import com.voyagrr.storageservice.enumeration.EncodingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

    @ManyToOne
    @JoinColumn(name = "directory_id", nullable = false)
    private Directory directory;

    @Column(name = "mini_object_key")
    private String minioObjectKey;

    @Column(name = "mime_type")
    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "encoding_status")
    private EncodingStatus encodingStatus = EncodingStatus.PENDING;

}
