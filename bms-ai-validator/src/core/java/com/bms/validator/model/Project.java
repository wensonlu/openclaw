package com.bms.validator.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 项目实体
 */
@Data
public class Project {
    private String id;
    private String name;
    private ProjectType type;
    private String repoUrl;
    private String framework;
    private ProjectSettings settings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum ProjectType {
        FRONTEND, BACKEND, FULLSTACK, MOBILE
    }

    @Data
    public static class ProjectSettings {
        private String buildCommand;
        private String testCommand;
        private String baseUrl;
        private String[] ignorePaths;
        private long timeout;
        private int retryCount;
    }

    public static Project create(String name, ProjectType type) {
        Project project = new Project();
        project.setId(UUID.randomUUID().toString());
        project.setName(name);
        project.setType(type);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        return project;
    }
}