package com.bms.validator.scheduler;

import com.bms.validator.executor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Temporal 活动实现
 * 调用各个验收执行器完成具体工作
 */
@Component
public class AcceptanceActivitiesImpl implements AcceptanceActivities {

    private static final Logger logger = LoggerFactory.getLogger(AcceptanceActivitiesImpl.class);

    @Autowired
    private BuildExecutor buildExecutor;
    
    @Autowired
    private StaticExecutor staticExecutor;
    
    @Autowired
    private DynamicExecutor dynamicExecutor;
    
    @Autowired
    private SemanticExecutor semanticExecutor;
    
    @Autowired
    private com.bms.validator.reactor.ReActCorrectionEngine correctionEngine;

    @Override
    public BuildResult executeBuildAcceptance(String projectId, String featureId, String commitId) {
        logger.info("执行构建验收: projectId={}, featureId={}, commitId={}", projectId, featureId, commitId);
        try {
            BuildExecutor.BuildOutput output = buildExecutor.execute(projectId, featureId, commitId);
            
            BuildResult result = new BuildResult();
            result.setSuccess(output.isSuccess());
            result.setBuildLog(output.getLog());
            result.setArtifactPath(output.getArtifactPath());
            result.setErrorMessage(output.getErrorMessage());
            return result;
        } catch (Exception e) {
            logger.error("构建验收异常", e);
            BuildResult result = new BuildResult();
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            return result;
        }
    }

    @Override
    public StaticResult executeStaticAcceptance(String projectId, String featureId) {
        logger.info("执行静态验收: projectId={}, featureId={}", projectId, featureId);
        try {
            StaticExecutor.StaticOutput output = staticExecutor.execute(projectId, featureId);
            
            StaticResult result = new StaticResult();
            result.setSuccess(output.isSuccess());
            result.setSimilarity(output.getSimilarity());
            result.setDiffReport(output.getDiffReport());
            result.setErrorMessage(output.getErrorMessage());
            return result;
        } catch (Exception e) {
            logger.error("静态验收异常", e);
            StaticResult result = new StaticResult();
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            return result;
        }
    }

    @Override
    public DynamicResult executeDynamicAcceptance(String projectId, String featureId) {
        logger.info("执行动态验收: projectId={}, featureId={}", projectId, featureId);
        try {
            DynamicExecutor.DynamicOutput output = dynamicExecutor.execute(projectId, featureId);
            
            DynamicResult result = new DynamicResult();
            result.setSuccess(output.isSuccess());
            result.setPassCount(output.getPassCount());
            result.setFailCount(output.getFailCount());
            result.setErrorMessage(output.getErrorMessage());
            return result;
        } catch (Exception e) {
            logger.error("动态验收异常", e);
            DynamicResult result = new DynamicResult();
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            return result;
        }
    }

    @Override
    public SemanticResult executeSemanticAcceptance(String projectId, String featureId) {
        logger.info("执行语义验收: projectId={}, featureId={}", projectId, featureId);
        try {
            SemanticExecutor.SemanticOutput output = semanticExecutor.execute(projectId, featureId);
            
            SemanticResult result = new SemanticResult();
            result.setSuccess(output.isSuccess());
            result.setBusinessValidationReport(output.getValidationReport());
            result.setErrorMessage(output.getErrorMessage());
            return result;
        } catch (Exception e) {
            logger.error("语义验收异常", e);
            SemanticResult result = new SemanticResult();
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            return result;
        }
    }

    @Override
    public CorrectionResult executeCorrection(String stageName, String errorMessage, 
                                               String projectId, String featureId) {
        logger.info("执行ReAct修正: stage={}, error={}", stageName, errorMessage);
        try {
            ReActCorrectionEngine.CorrectionOutput output = correctionEngine.correct(
                stageName, errorMessage, projectId, featureId
            );
            
            CorrectionResult result = new CorrectionResult();
            result.setSuccess(output.isSuccess());
            result.setMessage(output.getMessage());
            result.setCodeChanges(output.getCodeChanges());
            return result;
        } catch (Exception e) {
            logger.error("ReAct修正异常", e);
            CorrectionResult result = new CorrectionResult();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }
}