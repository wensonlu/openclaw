package com.bms.validator.generator;

import com.bms.validator.model.*;
import java.util.List;
import java.util.UUID;

/**
 * 验收用例生成器
 * 使用 LLM 自动生成验收用例
 */
public class AcceptanceTestGenerator {

    private final LLMClient llmClient;

    public AcceptanceTestGenerator(LLMClient llmClient) {
        this.llmClient = llmClient;
    }

    /**
     * 生成验收用例
     */
    public List<AcceptanceTest> generate(String featureId, String requirementText, 
                                          AcceptanceTest.TestType[] testTypes) {
        try {
            // 1. 解析需求，提取功能点
            List<FeaturePoint> featurePoints = parseRequirements(requirementText);
            
            // 2. 为每个测试类型生成用例
            List<AcceptanceTest> tests = new java.util.ArrayList<>();
            for (AcceptanceTest.TestType testType : testTypes) {
                List<AcceptanceTest> typeTests = generateForType(featureId, featurePoints, testType);
                tests.addAll(typeTests);
            }
            
            return tests;
        } catch (Exception e) {
            throw new RuntimeException("用例生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析需求文档
     */
    private List<FeaturePoint> parseRequirements(String requirementText) {
        String prompt = String.format("""
            请分析以下需求文档，提取功能点。
            
            需求文档:
            %s
            
            请以 JSON 数组格式输出功能点，每个功能点包含：
            - name: 功能名称
            - description: 功能描述
            - category: 功能分类（CRUD/PERMISSION/WORKFLOW/INTEGRATION/EDGE_CASE）
            """, requirementText);
        
        String result = llmClient.generate(prompt);
        // TODO: 解析 JSON
        return List.of();
    }

    /**
     * 为指定测试类型生成用例
     */
    private List<AcceptanceTest> generateForType(String featureId, List<FeaturePoint> featurePoints,
                                                  AcceptanceTest.TestType testType) {
        String prompt = buildGenerationPrompt(featurePoints, testType);
        String result = llmClient.generate(prompt);
        
        // TODO: 解析结果，生成用例
        return List.of();
    }

    /**
     * 构建生成 Prompt
     */
    private String buildGenerationPrompt(List<FeaturePoint> featurePoints, 
                                         AcceptanceTest.TestType testType) {
        String testTypeDesc = getTestTypeDescription(testType);
        
        return String.format("""
            你是一个测试用例生成专家。请根据以下功能点，为%s生成验收用例。
            
            功能点:
            %s
            
            测试类型说明:
            %s
            
            请生成结构化的测试用例，包含：
            1. 用例名称
            2. 用例描述
            3. 测试步骤（包含操作类型、目标、预期结果）
            4. 断言配置
            
            输出格式: JSON 数组
            """, 
            testTypeDesc,
            featurePoints.stream()
                .map(fp -> "- " + fp.getName() + ": " + fp.getDescription())
                .reduce((a, b) -> a + "\n" + b)
                .orElse(""),
            testTypeDesc
        );
    }

    /**
     * 获取测试类型描述
     */
    private String getTestTypeDescription(AcceptanceTest.TestType testType) {
        return switch (testType) {
            case BUILD -> "构建验收 - 验证代码能否正常编译、构建、打包";
            case STATIC -> "静态验收 - 验证 UI 与设计稿的像素级、布局级、视觉级一致性";
            case DYNAMIC -> "动态验收 - 验证交互逻辑、流程跳转、功能点是否正常工作";
            case SEMANTIC -> "语义验收 - 验证业务逻辑、数据一致性、规则校验是否正确";
        };
    }

    /**
     * 功能点
     */
    static class FeaturePoint {
        private String name;
        private String description;
        private String category;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    interface LLMClient {
        String generate(String prompt);
    }
}