package com.bms.validator.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 测试执行记录实体
 */
@Data
public class TestRun {
    private String id;
    private String testId;
    private int runNumber;
    
    // 执行上下文
    private String commitId;
    private String branch;
    private String triggeredBy;
    
    // 时间
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long duration;
    
    // 执行结果
    private RunStatus status;
    private TestResult result;
    
    // 环境信息
    private RunEnvironment environment;
    
    // 重试/修正信息
    private int retryCount;
    private List<Correction> corrections;
    
    // 产物
    private List<Artifact> artifacts;

    public enum RunStatus {
        PENDING, RUNNING, PASSED, FAILED, TIMEOUT, SKIPPED
    }

    @Data
    public static class RunEnvironment {
        private String os;
        private String browser;
        private Viewport viewport;
        private String baseUrl;
        private Map<String, String> headers;

        @Data
        public static class Viewport {
            private int width;
            private int height;
            private Integer deviceScaleFactor;
        }
    }

    @Data
    public static class TestResult {
        private String id;
        private String runId;
        
        // 汇总
        private ResultStatus status;
        private int passCount;
        private int failCount;
        private int skipCount;
        
        // 步骤结果
        private List<StepResult> stepResults;
        
        // 失败信息
        private FailureInfo failureInfo;
        
        // 截图和日志
        private List<Screenshot> screenshots;
        private List<LogEntry> logs;
        
        // 性能数据
        private ExecutionMetrics metrics;

        public enum ResultStatus {
            PASS, FAIL, PARTIAL
        }
    }

    @Data
    public static class StepResult {
        private String stepId;
        private ResultStatus status;
        private long duration;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        
        // 执行详情
        private String actualAction;
        private Object actualValue;
        private String error;
        
        // 断言结果
        private List<AssertionResult> assertionResults;
        
        // 产物
        private String screenshot;

        public enum ResultStatus {
            PASS, FAIL, SKIP
        }
    }

    @Data
    public static class AssertionResult {
        private String assertionId;
        private ResultStatus status;
        private Object actualValue;
        private Object expectedValue;
        private String message;

        public enum ResultStatus {
            PASS, FAIL
        }
    }

    @Data
    public static class FailureInfo {
        private String failedStepId;
        private String failedAssertionId;
        private String errorType;
        private String errorMessage;
        private String stackTrace;
        private String screenshot;
    }

    @Data
    public static class Screenshot {
        private String stepId;
        private ScreenshotType type;
        private String url;
        private LocalDateTime timestamp;

        public enum ScreenshotType {
            before, after, error, baseline, actual
        }
    }

    @Data
    public static class LogEntry {
        private LocalDateTime timestamp;
        private LogLevel level;
        private String message;
        private String source;

        public enum LogLevel {
            DEBUG, INFO, WARN, ERROR
        }
    }

    @Data
    public static class ExecutionMetrics {
        private Long pageLoadTime;
        private Long firstContentfulPaint;
        private Long domContentLoaded;
        private Long resourceLoadTime;
        private Long apiResponseTime;
    }

    @Data
    public static class Correction {
        private String id;
        private String runId;
        private int attemptNumber;
        private String triggeredBy;
        private FailureAnalysis failureAnalysis;
        private CorrectionStrategy strategy;
        private List<CodeChange> changes;
        private String result;
        private String newError;
        private LocalDateTime createdAt;
        private long duration;

        @Data
        public static class FailureAnalysis {
            private String errorType;
            private String errorMessage;
            private String rootCause;
            private String relatedCode;
            private List<String> similarCases;
        }

        @Data
        public static class CorrectionStrategy {
            private String approach;
            private String prompt;
            private String model;
            private String reasoning;
        }

        @Data
        public static class CodeChange {
            private String file;
            private String changeType;
            private String before;
            private String after;
            private String diff;
        }
    }

    @Data
    public static class Artifact {
        private String type;
        private String path;
        private String url;
    }

    public static TestRun create(String testId, String commitId) {
        TestRun run = new TestRun();
        run.setId(UUID.randomUUID().toString());
        run.setTestId(testId);
        run.setRunNumber(1);
        run.setCommitId(commitId);
        run.setStatus(RunStatus.PENDING);
        run.setStartTime(LocalDateTime.now());
        run.setRetryCount(0);
        return run;
    }
}