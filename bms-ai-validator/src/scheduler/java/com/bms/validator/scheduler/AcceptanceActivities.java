package com.bms.validator.scheduler;

/**
 * Temporal 活动接口
 * 定义验收工作流中的各个活动
 */
public interface AcceptanceActivities {

    /**
     * 阶段1: 构建验收
     */
    BuildResult executeBuildAcceptance(String projectId, String featureId, String commitId);

    /**
     * 阶段2: 静态验收
     */
    StaticResult executeStaticAcceptance(String projectId, String featureId);

    /**
     * 阶段3: 动态验收
     */
    DynamicResult executeDynamicAcceptance(String projectId, String featureId);

    /**
     * 阶段4: 语义验收
     */
    SemanticResult executeSemanticAcceptance(String projectId, String featureId);

    /**
     * ReAct 修正活动
     */
    CorrectionResult executeCorrection(String stageName, String errorMessage, 
                                        String projectId, String featureId);
}

/**
 * 构建验收结果
 */
class BuildResult {
    private boolean success;
    private String errorMessage;
    private String buildLog;
    private String artifactPath;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getBuildLog() { return buildLog; }
    public void setBuildLog(String buildLog) { this.buildLog = buildLog; }
    public String getArtifactPath() { return artifactPath; }
    public void setArtifactPath(String artifactPath) { this.artifactPath = artifactPath; }
}

/**
 * 静态验收结果
 */
class StaticResult {
    private boolean success;
    private String errorMessage;
    private double similarity;
    private String diffReport;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public double getSimilarity() { return similarity; }
    public void setSimilarity(double similarity) { this.similarity = similarity; }
    public String getDiffReport() { return diffReport; }
    public void setDiffReport(String diffReport) { this.diffReport = diffReport; }
}

/**
 * 动态验收结果
 */
class DynamicResult {
    private boolean success;
    private String errorMessage;
    private int passCount;
    private int failCount;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public int getPassCount() { return passCount; }
    public void setPassCount(int passCount) { this.passCount = passCount; }
    public int getFailCount() { return failCount; }
    public void setFailCount(int failCount) { this.failCount = failCount; }
}

/**
 * 语义验收结果
 */
class SemanticResult {
    private boolean success;
    private String errorMessage;
    private String businessValidationReport;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getBusinessValidationReport() { return businessValidationReport; }
    public void setBusinessValidationReport(String businessValidationReport) { this.businessValidationReport = businessValidationReport; }
}

/**
 * 修正结果
 */
class CorrectionResult {
    private boolean success;
    private String message;
    private String codeChanges;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getCodeChanges() { return codeChanges; }
    public void setCodeChanges(String codeChanges) { this.codeChanges = codeChanges; }
}