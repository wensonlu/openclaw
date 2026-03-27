package com.bms.validator.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 功能实体
 */
@Data
public class Feature {
    private String id;
    private String projectId;
    private String name;
    private String description;
    private String requirementDoc;
    private Priority priority;
    private FeatureStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Priority {
        P0, P1, P2, P3
    }

    public enum FeatureStatus {
        DRAFT, IN_PROGRESS, TESTING, COMPLETED, ARCHIVED
    }

    public static Feature create(String projectId, String name, String description) {
        Feature feature = new Feature();
        feature.setId(UUID.randomUUID().toString());
        feature.setProjectId(projectId);
        feature.setName(name);
        feature.setDescription(description);
        feature.setPriority(Priority.P1);
        feature.setStatus(FeatureStatus.DRAFT);
        feature.setCreatedAt(LocalDateTime.now());
        feature.setUpdatedAt(LocalDateTime.now());
        return feature;
    }
}