package com.voyagrr.storageservice.model;

import com.voyagrr.storageservice.config.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "directories")
public class Directory extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String ownerId;

    @ManyToOne
    @JoinColumn(name = "parent_directory_id")
    private Directory parentDirectory;

    @Column(nullable = false)
    private short type;

}
