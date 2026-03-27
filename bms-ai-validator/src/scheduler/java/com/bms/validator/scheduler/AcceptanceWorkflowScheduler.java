package com.bms.validator.scheduler;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.stereotype.Component;

/**
 * Temporal 工作流调度器
 * 负责管理验收流程的长链路任务编排
 */
@Component
public class AcceptanceWorkflowScheduler {

    private final WorkflowServiceStubs workflowService;
    private final WorkflowClient workflowClient;
    private final WorkerFactory workerFactory;

    public AcceptanceWorkflowScheduler() {
        // 初始化 Temporal 客户端
        this.workflowService = WorkflowServiceStubs.newLocalServiceStubs();
        this.workflowClient = WorkflowClient.newInstance(workflowService);
        this.workerFactory = WorkerFactory.newInstance(workflowClient);
    }

    /**
     * 启动验收工作流
     * 依次执行：构建验收 → 静态验收 → 动态验收 → 语义验收
     */
    public String startAcceptanceWorkflow(String projectId, String featureId, String commitId) {
        AcceptanceWorkflow workflow = workflowClient.newWorkflowStub(
            AcceptanceWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue("acceptance-queue")
                .setWorkflowId("acceptance-" + featureId + "-" + System.currentTimeMillis())
                .build()
        );

        // 异步启动工作流
        WorkflowClient.start(workflow::run, projectId, featureId, commitId);
        
        return workflowClient.getWorkflowId();
    }

    /**
     * 注册 Worker
     * 处理验收工作流的任务队列
     */
    public void registerWorker() {
        Worker worker = workerFactory.newWorker("acceptance-queue");
        worker.registerWorkflowImplementationTypes(AcceptanceWorkflowImpl.class);
        worker.registerActivitiesImplementation(new AcceptanceActivitiesImpl());
        workerFactory.start();
    }

    /**
     * 获取工作流状态
     */
    public WorkflowStatus getWorkflowStatus(String workflowId) {
        // 查询工作流状态
        return workflowClient.newWorkflowStub(
            AcceptanceWorkflow.class,
            workflowId
        ).getStatus();
    }

    /**
     * 取消工作流
     */
    public void cancelWorkflow(String workflowId) {
        workflowClient.newWorkflowStub(AcceptanceWorkflow.class, workflowId)
            .cancel();
    }
}