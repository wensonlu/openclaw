package com.bms.validator.reactor;

import com.bms.validator.llm.LlmClientImpl;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * ReAct 修正引擎
 * 根据失败类型调用不同的修复策略
 */
@Component
public class ReActCorrectionEngine {

    private final LlmClientImpl llmClient;
    private final CodeModifierImpl codeModifier;

    @Autowired
    public ReActCorrectionEngine(LlmClientImpl llmClient, CodeModifierImpl codeModifier) {
        this.llmClient = llmClient;
        this.codeModifier = codeModifier;
    }

    /**
     * 执行修正
     */
    public CorrectionOutput correct(String stageName, String errorMessage, 
                                     String projectId, String featureId) {
        CorrectionOutput output = new CorrectionOutput();
        
        try {
            // 1. 分析失败类型
            FailureType failureType = analyzeFailureType(stageName, errorMessage);
            
            // 2. 根据失败类型选择修复策略
            String fixPrompt = buildFixPrompt(failureType, errorMessage, projectId, featureId);
            
            // 3. 调用 LLM 生成修复方案
            String fixPlan = llmClient.generate(fixPrompt);
            
            // 4. 执行代码修改
            String codeChanges = codeModifier.applyChanges(projectId, fixPlan);
            
            output.setSuccess(true);
            output.setMessage("修正成功");
            output.setCodeChanges(codeChanges);
            
            return output;
        } catch (Exception e) {
            output.setSuccess(false);
            output.setMessage(e.getMessage());
            return output;
        }
    }

    /**
     * 分析失败类型
     */
    private FailureType analyzeFailureType(String stageName, String errorMessage) {
        Map<String, FailureType> stageToFailureType = new HashMap<>();
        stageToFailureType.put("BUILD", FailureType.BUILD_ERROR);
        stageToFailureType.put("STATIC", FailureType.STATIC_ERROR);
        stageToFailureType.put("DYNAMIC", FailureType.DYNAMIC_ERROR);
        stageToFailureType.put("SEMANTIC", FailureType.SEMANTIC_ERROR);
        
        return stageToFailureType.getOrDefault(stageName, FailureType.UNKNOWN);
    }

    /**
     * 构建修复 Prompt
     */
    private String buildFixPrompt(FailureType failureType, String errorMessage,
                                  String projectId, String featureId) {
        String systemPrompt = getSystemPrompt(failureType);
        
        return String.format("""
            %s

            ## 场景
            项目ID: %s
            功能ID: %s
            错误信息: %s

            ## 分析任务
            1. 仔细分析错误信息，定位问题根因
            2. 提出具体的修复方案
            3. 给出修改的代码 diff

            ## 输出格式
            ```diff
            // 修改的代码
            ```

            ## 注意事项
            - 每次修复只解决一个问题
            - 修复后需要验证
            - 保持代码风格一致
            """, systemPrompt, projectId, featureId, errorMessage);
    }

    /**
     * 获取不同失败类型的系统 Prompt
     */
    private String getSystemPrompt(FailureType failureType) {
        Map<FailureType, String> prompts = new HashMap<>();
        
        prompts.put(FailureType.BUILD_ERROR, """
            你是一个代码修复助手，专门解决构建失败问题。
            常见问题包括：编译错误、依赖冲突、配置问题。
            请分析错误信息，给出具体的修复方案。
            """);
        
        prompts.put(FailureType.STATIC_ERROR, """
            你是一个 UI 修复助手，专门解决静态验收失败问题。
            常见问题包括：布局偏差、样式差异、元素缺失。
            请分析设计稿和实际截图的差异，给出 CSS 调整方案。
            """);
        
        prompts.put(FailureType.DYNAMIC_ERROR, """
            你是一个交互修复助手，专门解决动态验收失败问题。
            常见问题包括：交互失败、流程中断、权限问题。
            请分析失败原因，给出修复交互逻辑的方案。
            """);
        
        prompts.put(FailureType.SEMANTIC_ERROR, """
            你是一个业务逻辑修复助手，专门解决语义验收失败问题。
            常见问题包括：业务逻辑错误、数据不一致、规则校验失败。
            请分析业务规则和实际执行结果的差异，给出修复方案。
            """);
        
        prompts.put(FailureType.UNKNOWN, """
            你是一个代码修复助手。请分析错误信息，找出问题的根本原因，并给出修复方案。
            """);
        
        return prompts.getOrDefault(failureType, prompts.get(FailureType.UNKNOWN));
    }

    @Data
    public static class CorrectionOutput {
        private boolean success;
        private String message;
        private String codeChanges;
    }

    public enum FailureType {
        BUILD_ERROR,
        STATIC_ERROR,
        DYNAMIC_ERROR,
        SEMANTIC_ERROR,
        UNKNOWN
    }
}