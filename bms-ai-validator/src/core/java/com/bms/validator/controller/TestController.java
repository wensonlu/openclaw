package com.bms.validator.controller;

import com.bms.validator.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 验收测试 API
 */
@RestController
@RequestMapping("/api/v1/tests")
public class TestController {

    /**
     * 生成验收用例
     */
    @PostMapping("/generate")
    public ResponseEntity<GenerateTestsResponse> generateTests(@RequestBody GenerateTestsRequest request) {
        // TODO: 调用用例生成器
        GenerateTestsResponse response = new GenerateTestsResponse();
        response.setTests(List.of());
        response.setModel("gpt-4");
        return ResponseEntity.ok(response);
    }

    /**
     * 执行验收测试
     */
    @PostMapping("/{id}/run")
    public ResponseEntity<RunTestResponse> runTest(@PathVariable String id, 
                                                    @RequestBody RunTestRequest request) {
        // TODO: 调用工作流调度器启动验收
        RunTestResponse response = new RunTestResponse();
        response.setRunId("run-" + System.currentTimeMillis());
        response.setStatus(TestRun.RunStatus.PENDING);
        return ResponseEntity.accepted().body(response);
    }

    /**
     * 获取验收用例详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<AcceptanceTest> getTest(@PathVariable String id) {
        // TODO: 从数据库查询
        return ResponseEntity.ok(new AcceptanceTest());
    }

    /**
     * 获取测试步骤
     */
    @GetMapping("/{id}/steps")
    public ResponseEntity<List<TestStep>> getTestSteps(@PathVariable String id) {
        // TODO: 从数据库查询
        return ResponseEntity.ok(List.of());
    }

    static class GenerateTestsRequest {
        private String projectId;
        private String featureId;
        private String requirementText;
        private AcceptanceTest.TestType[] testTypes;

        public String getProjectId() { return projectId; }
        public void setProjectId(String projectId) { this.projectId = projectId; }
        public String getFeatureId() { return featureId; }
        public void setFeatureId(String featureId) { this.featureId = featureId; }
        public String getRequirementText() { return requirementText; }
        public void setRequirementText(String requirementText) { this.requirementText = requirementText; }
        public AcceptanceTest.TestType[] getTestTypes() { return testTypes; }
        public void setTestTypes(AcceptanceTest.TestType[] testTypes) { this.testTypes = testTypes; }
    }

    static class GenerateTestsResponse {
        private List<AcceptanceTest> tests;
        private String model;
        private long duration;

        public List<AcceptanceTest> getTests() { return tests; }
        public void setTests(List<AcceptanceTest> tests) { this.tests = tests; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }
    }

    static class RunTestRequest {
        private String commitId;
        private Map<String, String> environment;

        public String getCommitId() { return commitId; }
        public void setCommitId(String commitId) { this.commitId = commitId; }
        public Map<String, String> getEnvironment() { return environment; }
        public void setEnvironment(Map<String, String> environment) { this.environment = environment; }
    }

    static class RunTestResponse {
        private String runId;
        private TestRun.RunStatus status;

        public String getRunId() { return runId; }
        public void setRunId(String runId) { this.runId = runId; }
        public TestRun.RunStatus getStatus() { return status; }
        public void setStatus(TestRun.RunStatus status) { this.status = status; }
    }
}