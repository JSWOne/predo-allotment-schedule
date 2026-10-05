package com.jswone.orchestrator.jobs.orderRelease.service;

import com.jswone.orchestrator.dto.OrchestratorResponse;
import com.jswone.orchestrator.jobs.orderRelease.workflow.AutoSoClosureWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowIdReusePolicy;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoSoClosureServiceImpl implements AutoSoClosureService {

  private final WorkflowClient workflowClient;

  @Value("${temporal.pre-do-allotment-task-queue}")
  private String temporalTaskQueue;

  @Override
  public OrchestratorResponse initiateAutoSoClosureScheduler() {
    log.info("Workflow to be triggered for initiateAutoSoClosureScheduler");
    String workflowId = "auto-so-closure-" + LocalDateTime.now();

    WorkflowOptions options =
        WorkflowOptions.newBuilder()
            .setTaskQueue(temporalTaskQueue)
            .setWorkflowId(workflowId)
            .setWorkflowIdReusePolicy(
                WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE_FAILED_ONLY)
            .setWorkflowExecutionTimeout(Duration.ofMinutes(30))
            .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
            .build();

    AutoSoClosureWorkflow workflowStub =
        workflowClient.newWorkflowStub(AutoSoClosureWorkflow.class, options);

    WorkflowExecution execution = WorkflowClient.start(workflowStub::initiateAutoSoClosureJob);

    log.info(
        "Workflow triggered asynchronously for auto SO closure, workflowId={}, runId={}",
        workflowId,
        execution.getRunId());

    return OrchestratorResponse.builder()
        .isSuccess(true)
        .message("Workflow started successfully")
        .build();
  }
}
