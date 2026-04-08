package com.voyagrr.tripservice.model;

import com.voyagrr.common.enumeration.TripStatus;
import com.voyagrr.common.enumeration.TripVisibility;
import com.voyagrr.tripservice.config.entity.Auditable;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trip")
public class Trip extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String ownerId;

    @Column(nullable = false)
    private Long directoryId;

    @Builder.Default
    @Column(nullable = false)
    private Long groupId = 0L;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TripStatus status = TripStatus.PLANNED;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private TripVisibility visibility = TripVisibility.PRIVATE;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

}
