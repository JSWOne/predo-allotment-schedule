package com.jswone.orchestrator.jobs.orderRelease.service;

import com.jswone.orchestrator.dto.OrchestratorResponse;
import com.jswone.orchestrator.jobs.orderRelease.workflow.PreDoAllotmentWorkflow;
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
public class PreDoAllotmentServiceImpl implements PreDoAllotmentService {

  private final WorkflowClient workflowClient;

  @Value("${temporal.pre-do-allotment-task-queue}")
  private String temporalTaskQueue;

  @Override
  public OrchestratorResponse initiateFgPreDoAllotmentScheduler() {
    log.info("Workflow to be triggered for initiateFgPreDoAllotmentScheduler");
    String workflowId = "fg-pre-do-allotment-" + LocalDateTime.now();
    WorkflowOptions options = buildWorkflowOptions(workflowId);

    PreDoAllotmentWorkflow workflowStub =
        workflowClient.newWorkflowStub(PreDoAllotmentWorkflow.class, options);

    WorkflowExecution execution = WorkflowClient.start(workflowStub::initiatePreDoAllotmentJob);

    String runId = execution.getRunId();

    log.info(
        "Workflow triggered asynchronously for pre-do allotment, workflowId={}, runId={}",
        workflowId,
        runId);

    return OrchestratorResponse.builder()
        .isSuccess(true)
        .message("Workflow started successfully")
        .build();
  }

  public WorkflowOptions buildWorkflowOptions(String workflowId) {
    return WorkflowOptions.newBuilder()
        .setTaskQueue(temporalTaskQueue)
        .setWorkflowId(workflowId)
        .setWorkflowIdReusePolicy(
            WorkflowIdReusePolicy.WORKFLOW_ID_REUSE_POLICY_ALLOW_DUPLICATE_FAILED_ONLY)
        .setWorkflowExecutionTimeout(Duration.ofMinutes(30))
        .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
        .build();
  }

  // Schedules manage ID reuse internally; WorkflowIdReusePolicy must not be set here.
  public WorkflowOptions buildScheduledWorkflowOptions(String workflowId) {
    return WorkflowOptions.newBuilder()
        .setTaskQueue(temporalTaskQueue)
        .setWorkflowId(workflowId)
        .setWorkflowExecutionTimeout(Duration.ofMinutes(30))
        .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
        .build();
  }

  public String getTemporalTaskQueue() {
    return temporalTaskQueue;
  }
}
