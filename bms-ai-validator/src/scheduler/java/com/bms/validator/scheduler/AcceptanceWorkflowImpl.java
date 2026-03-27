package com.bms.validator.scheduler;

import io.temporal.workflow.Workflow;
import io.temporal.activity.ActivityOptions;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 验收工作流实现
 * 按照 四层验收 + ReAct 修正 的流程执行
 */
public class AcceptanceWorkflowImpl implements AcceptanceWorkflow {

    // 活动调用选项
    private static final ActivityOptions DEFAULT_ACTIVITY_OPTIONS = ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofMinutes(10))
        .setRetryOptions(io.temporal.common.RetryOptions.newBuilder()
            .setMaximumAttempts(3)
            .build())
        .build();

    private final AcceptanceActivities activities = Workflow.newActivityStub(
        AcceptanceActivities.class, 
        DEFAULT_ACTIVITY_OPTIONS
    );

    @Override
    public WorkflowResult run(String projectId, String featureId, String commitId) {
        long startTime = System.currentTimeMillis();
        List<StageResult> stageResults = new ArrayList<>();

        try {
            // 阶段1: 构建验收
            StageResult buildResult = executeBuildStage(projectId, featureId, commitId);
            stageResults.add(buildResult);
            
            if (!buildResult.isPassed()) {
                return buildFailureResult("构建验收失败", stageResults, startTime);
            }

            // 阶段2: 静态验收
            StageResult staticResult = executeStaticStage(projectId, featureId);
            stageResults.add(staticResult);
            
            if (!staticResult.isPassed()) {
                return buildFailureResult("静态验收失败", stageResults, startTime);
            }

            // 阶段3: 动态验收
            StageResult dynamicResult = executeDynamicStage(projectId, featureId);
            stageResults.add(dynamicResult);
            
            if (!dynamicResult.isPassed()) {
                return buildFailureResult("动态验收失败", stageResults, startTime);
            }

            // 阶段4: 语义验收
            StageResult semanticResult = executeSemanticStage(projectId, featureId);
            stageResults.add(semanticResult);
            
            if (!semanticResult.isPassed()) {
                return buildFailureResult("语义验收失败", stageResults, startTime);
            }

            // 全部通过
            return buildSuccessResult(stageResults, startTime);

        } catch (Exception e) {
            return buildFailureResult("工作流执行异常: " + e.getMessage(), stageResults, startTime);
        }
    }

    private StageResult executeBuildStage(String projectId, String featureId, String commitId) {
        long start = System.currentTimeMillis();
        try {
            BuildResult result = activities.executeBuildAcceptance(projectId, featureId, commitId);
            
            StageResult stageResult = new StageResult();
            stageResult.setStageName("BUILD");
            stageResult.setPassed(result.isSuccess());
            stageResult.setErrorMessage(result.getErrorMessage());
            stageResult.setDuration(System.currentTimeMillis() - start);
            stageResult.setResult(result);
            
            // ReAct 修正循环
            if (!result.isSuccess()) {
                return executeWithReAct("BUILD", stageResult, projectId, featureId);
            }
            
            return stageResult;
        } catch (Exception e) {
            StageResult stageResult = new StageResult();
            stageResult.setStageName("BUILD");
            stageResult.setPassed(false);
            stageResult.setErrorMessage(e.getMessage());
            stageResult.setDuration(System.currentTimeMillis() - start);
            return stageResult;
        }
    }

    private StageResult executeStaticStage(String projectId, String featureId) {
        long start = System.currentTimeMillis();
        try {
            StaticResult result = activities.executeStaticAcceptance(projectId, featureId);
            
            StageResult stageResult = new StageResult();
            stageResult.setStageName("STATIC");
            stageResult.setPassed(result.isSuccess());
            stageResult.setErrorMessage(result.getErrorMessage());
            stageResult.setDuration(System.currentTimeMillis() - start);
            stageResult.setResult(result);
            
            if (!result.isSuccess()) {
                return executeWithReAct("STATIC", stageResult, projectId, featureId);
            }
            
            return stageResult;
        } catch (Exception e) {
            StageResult stageResult = new StageResult();
            stageResult.setStageName("STATIC");
            stageResult.setPassed(false);
            stageResult.setErrorMessage(e.getMessage());
            stageResult.setDuration(System.currentTimeMillis() - start);
            return stageResult;
        }
    }

    private StageResult executeDynamicStage(String projectId, String featureId) {
        long start = System.currentTimeMillis();
        try {
            DynamicResult result = activities.executeDynamicAcceptance(projectId, featureId);
            
            StageResult stageResult = new StageResult();
            stageResult.setStageName("DYNAMIC");
            stageResult.setPassed(result.isSuccess());
            stageResult.setErrorMessage(result.getErrorMessage());
            stageResult.setDuration(System.currentTimeMillis() - start);
            stageResult.setResult(result);
            
            if (!result.isSuccess()) {
                return executeWithReAct("DYNAMIC", stageResult, projectId, featureId);
            }
            
            return stageResult;
        } catch (Exception e) {
            StageResult stageResult = new StageResult();
            stageResult.setStageName("DYNAMIC");
            stageResult.setPassed(false);
            stageResult.setErrorMessage(e.getMessage());
            stageResult.setDuration(System.currentTimeMillis() - start);
            return stageResult;
        }
    }

    private StageResult executeSemanticStage(String projectId, String featureId) {
        long start = System.currentTimeMillis();
        try {
            SemanticResult result = activities.executeSemanticAcceptance(projectId, featureId);
            
            StageResult stageResult = new StageResult();
            stageResult.setStageName("SEMANTIC");
            stageResult.setPassed(result.isSuccess());
            stageResult.setErrorMessage(result.getErrorMessage());
            stageResult.setDuration(System.currentTimeMillis() - start);
            stageResult.setResult(result);
            
            if (!result.isSuccess()) {
                return executeWithReAct("SEMANTIC", stageResult, projectId, featureId);
            }
            
            return stageResult;
        } catch (Exception e) {
            StageResult stageResult = new StageResult();
            stageResult.setStageName("SEMANTIC");
            stageResult.setPassed(false);
            stageResult.setErrorMessage(e.getMessage());
            stageResult.setDuration(System.currentTimeMillis() - start);
            return stageResult;
        }
    }

    /**
     * ReAct 修正循环
     * 失败后调用修正引擎，修复后重新执行该阶段
     */
    private StageResult executeWithReAct(String stageName, StageResult initialResult, 
                                          String projectId, String featureId) {
        int maxRetries = 3;
        StageResult currentResult = initialResult;

        for (int retry = 1; retry <= maxRetries; retry++) {
            // 调用 ReAct 修正引擎
            CorrectionResult correction = activities.executeCorrection(
                stageName, 
                currentResult.getErrorMessage(),
                projectId,
                featureId
            );

            if (correction.isSuccess()) {
                // 修复成功，重新执行该阶段
                currentResult = retryStage(stageName, projectId, featureId);
                if (currentResult.isPassed()) {
                    return currentResult;
                }
            } else {
                // 修正失败，记录并继续尝试
                System.out.println("ReAct修正第" + retry + "次失败: " + correction.getMessage());
            }
        }

        // 超过最大重试次数
        currentResult.setErrorMessage("ReAct修正超过最大次数(" + maxRetries + ")，需要人工介入");
        return currentResult;
    }

    private StageResult retryStage(String stageName, String projectId, String featureId) {
        switch (stageName) {
            case "BUILD":
                return executeBuildStage(projectId, featureId, null);
            case "STATIC":
                return executeStaticStage(projectId, featureId);
            case "DYNAMIC":
                return executeDynamicStage(projectId, featureId);
            case "SEMANTIC":
                return executeSemanticStage(projectId, featureId);
            default:
                return null;
        }
    }

    private WorkflowResult buildSuccessResult(List<StageResult> stageResults, long startTime) {
        WorkflowResult result = new WorkflowResult();
        result.setSuccess(true);
        result.setMessage("所有验收阶段通过");
        result.setStageResults(stageResults);
        result.setTotalDuration(System.currentTimeMillis() - startTime);
        return result;
    }

    private WorkflowResult buildFailureResult(String message, List<StageResult> stageResults, long startTime) {
        WorkflowResult result = new WorkflowResult();
        result.setSuccess(false);
        result.setMessage(message);
        result.setStageResults(stageResults);
        result.setTotalDuration(System.currentTimeMillis() - startTime);
        return result;
    }

    @Override
    public WorkflowStatus getStatus() {
        return WorkflowStatus.COMPLETED;
    }

    @Override
    public void cancel() {
        Workflow.workflowShutdown();
    }
}