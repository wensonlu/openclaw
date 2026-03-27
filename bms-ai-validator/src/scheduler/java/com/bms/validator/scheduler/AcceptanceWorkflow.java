package com.bms.validator.scheduler;

import io.temporal.workflow.Workflow;
import io.temporal.workflow.Async;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 验收工作流接口
 * 定义四层验收的流程编排
 */
public interface AcceptanceWorkflow {

    /**
     * 执行验收工作流
     * 依次执行：构建 → 静态 → 动态 → 语义
     */
    WorkflowResult run(String projectId, String featureId, String commitId);

    /**
     * 获取工作流状态
     */
    WorkflowStatus getStatus();

    /**
     * 取消工作流
     */
    void cancel();
}

/**
 * 工作流执行结果
 */
class WorkflowResult {
    private boolean success;
    private String message;
    private List<StageResult> stageResults;
    private long totalDuration;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<StageResult> getStageResults() {
        return stageResults;
    }

    public void setStageResults(List<StageResult> stageResults) {
        this.stageResults = stageResults;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }
}

/**
 * 各阶段验收结果
 */
class StageResult {
    private String stageName;
    private boolean passed;
    private String errorMessage;
    private long duration;
    private Object result;

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}

/**
 * 工作流状态
 */
enum WorkflowStatus {
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELED
}