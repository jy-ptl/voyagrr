package com.voyagrr.storageservice.model;

import com.voyagrr.storageservice.config.Auditable;
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
@Table(name = "directories")
public class Directory extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String ownerId;

    @ManyToOne
    @JoinColumn(name = "parent_directory_id")
    private Directory parentDirectory;


}
