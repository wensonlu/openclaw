package com.bms.validator.controller;

import com.bms.validator.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 功能管理 API
 */
@RestController
@RequestMapping("/api/v1/features")
public class FeatureController {

    /**
     * 创建功能
     */
    @PostMapping
    public ResponseEntity<Feature> createFeature(@RequestBody CreateFeatureRequest request) {
        Feature feature = Feature.create(request.getProjectId(), request.getName(), request.getDescription());
        feature.setRequirementDoc(request.getRequirementDoc());
        feature.setPriority(request.getPriority());
        // TODO: 保存到数据库
        return ResponseEntity.ok(feature);
    }

    /**
     * 获取功能列表
     */
    @GetMapping
    public ResponseEntity<List<Feature>> listFeatures(@RequestParam String projectId) {
        // TODO: 从数据库查询
        return ResponseEntity.ok(List.of());
    }

    /**
     * 获取功能详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Feature> getFeature(@PathVariable String id) {
        // TODO: 从数据库查询
        return ResponseEntity.ok(new Feature());
    }

    static class CreateFeatureRequest {
        private String projectId;
        private String name;
        private String description;
        private String requirementDoc;
        private Feature.Priority priority;

        public String getProjectId() { return projectId; }
        public void setProjectId(String projectId) { this.projectId = projectId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getRequirementDoc() { return requirementDoc; }
        public void setRequirementDoc(String requirementDoc) { this.requirementDoc = requirementDoc; }
        public Feature.Priority getPriority() { return priority; }
        public void setPriority(Feature.Priority priority) { this.priority = priority; }
    }
}