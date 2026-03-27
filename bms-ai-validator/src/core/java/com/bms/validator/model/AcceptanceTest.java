package com.bms.validator.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.Map;

/**
 * 验收用例实体
 */
@Data
public class AcceptanceTest {
    private String id;
    private String featureId;
    private String name;
    private String description;
    
    // 用例分类
    private TestType testType;
    private TestCategory testCategory;
    private List<String> tags;
    
    // 执行配置
    private ExecutionConfig executionConfig;
    
    // 生成信息
    private String generatedBy;
    private String generationPrompt;
    private String sourceRequirement;
    
    // 状态管理
    private TestStatus status;
    private int version;
    
    // 时间戳
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum TestType {
        BUILD, STATIC, DYNAMIC, SEMANTIC
    }

    public enum TestCategory {
        CRUD, PERMISSION, WORKFLOW, INTEGRATION, EDGE_CASE, PERFORMANCE, REGRESSION
    }

    public enum TestStatus {
        DRAFT, ACTIVE, DEPRECATED, MAINTENANCE
    }

    @Data
    public static class ExecutionConfig {
        private long timeout;
        private int retryCount;
        private int priority;
        private boolean parallelizable;
        private List<String> dependencies;
        private String environment;
    }

    public static AcceptanceTest create(String featureId, String name, TestType testType) {
        AcceptanceTest test = new AcceptanceTest();
        test.setId(UUID.randomUUID().toString());
        test.setFeatureId(featureId);
        test.setName(name);
        test.setTestType(testType);
        test.setStatus(TestStatus.DRAFT);
        test.setVersion(1);
        test.setCreatedAt(LocalDateTime.now());
        test.setUpdatedAt(LocalDateTime.now());
        return test;
    }
}