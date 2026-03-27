package com.bms.validator.controller;

import com.bms.validator.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * 项目管理 API
 */
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    /**
     * 创建项目
     */
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody CreateProjectRequest request) {
        Project project = Project.create(request.getName(), request.getType());
        project.setRepoUrl(request.getRepoUrl());
        project.setFramework(request.getFramework());
        // TODO: 保存到数据库
        return ResponseEntity.ok(project);
    }

    /**
     * 获取项目列表
     */
    @GetMapping
    public ResponseEntity<List<Project>> listProjects() {
        // TODO: 从数据库查询
        return ResponseEntity.ok(List.of());
    }

    /**
     * 获取项目详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProject(@PathVariable String id) {
        // TODO: 从数据库查询
        return ResponseEntity.ok(new Project());
    }

    static class CreateProjectRequest {
        private String name;
        private Project.ProjectType type;
        private String repoUrl;
        private String framework;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Project.ProjectType getType() { return type; }
        public void setType(Project.ProjectType type) { this.type = type; }
        public String getRepoUrl() { return repoUrl; }
        public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }
        public String getFramework() { return framework; }
        public void setFramework(String framework) { this.framework = framework; }
    }
}