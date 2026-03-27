package com.bms.validator.model;

import lombok.Data;
import java.util.Map;
import java.util.UUID;

/**
 * 测试步骤实体
 */
@Data
public class TestStep {
    private String id;
    private String testId;
    private int order;
    private String name;
    
    // 步骤类型和内容
    private StepType stepType;
    private StepAction action;
    private StepTarget target;
    private Map<String, Object> parameters;
    private WaitCondition waitCondition;
    
    // 预期结果
    private ExpectedResult expectedResult;
    
    // 截图/日志配置
    private boolean captureScreenshot;
    private boolean captureLogs;
    private Map<String, Object> metadata;

    public enum StepType {
        NAVIGATE, INTERACT, VERIFY, WAIT, API, DATA_CHECK
    }

    public enum StepAction {
        // 导航
        goto_, click, doubleClick, rightClick,
        // 输入
        type, select, check, uncheck,
        // 操作
        submit, upload, download, drag,
        // 等待
        waitForElement, waitForResponse, waitForNavigation,
        // API
        httpGet, httpPost, httpPut, httpDelete,
        // 数据校验
        assertEqual, assertContain, assertMatch, assertCount
    }

    @Data
    public static class StepTarget {
        private StepTargetType type;
        private String value;
        private String frame;

        public enum StepTargetType {
            selector, xpath, text, role, url, api
        }
    }

    @Data
    public static class WaitCondition {
        private WaitConditionType type;
        private long timeout;
        private String value;

        public enum WaitConditionType {
            visibility, clickable, hidden, text, response
        }
    }

    @Data
    public static class ExpectedResult {
        private ExpectedResultType type;
        private MatchType matchType;
        private Object value;
        private String screenshotBaseline;

        public enum ExpectedResultType {
            element, text, url, api, screenshot, database
        }

        public enum MatchType {
            exact, contain, regex, visual, schema
        }
    }

    public static TestStep create(String testId, int order, String name, StepType stepType, StepAction action) {
        TestStep step = new TestStep();
        step.setId(UUID.randomUUID().toString());
        step.setTestId(testId);
        step.setOrder(order);
        step.setName(name);
        step.setStepType(stepType);
        step.setAction(action);
        step.setCaptureScreenshot(false);
        step.setCaptureLogs(true);
        return step;
    }
}