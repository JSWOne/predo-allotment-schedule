package com.jswone.orchestrator.jobs.orderRelease.service;

import com.jswone.orchestrator.dto.OrchestratorResponse;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import com.jswone.orchestrator.jobs.orderRelease.workflow.DueNotificationWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DueNotificationServiceImpl implements DueNotificationService {

  private final WorkflowClient workflowClient;

  @Value("${temporal.due-notification-task-queue}")
  private String temporalDueNotificationTaskQueue;

  @Override
  public OrchestratorResponse initiateOverDueNotificationWorkflow(
      NotificationEventType notificationEventType) {
    log.info("Workflow to be triggered for confirming order {}");
    WorkflowOptions options =
        WorkflowOptions.newBuilder()
            .setTaskQueue(temporalDueNotificationTaskQueue)
            .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
            .build();
    /* DueNotificationWorkflow sellerPOWorkflow =
        workflowClient.newWorkflowStub(DueNotificationWorkflow.class, options);

    OrchestratorResponse response = sellerPOWorkflow.initiateOverDueJob(notificationEventType);*/

    // CreateOrderWorkflow wf = workflowClient.newWorkflowStub(CreateOrderWorkflow.class, options);
    /*
        WorkflowExecution exec =
            io.temporal.client.WorkflowClient.start(
                sellerPOWorkflow::initiateSellerPOJob, sellerPOWorkflowInitiationRequest);
    */
    DueNotificationWorkflow workflowStub =
        workflowClient.newWorkflowStub(DueNotificationWorkflow.class, options);

    // ✅ ASYNC START (NON-BLOCKING)
    WorkflowExecution execution =
        WorkflowClient.start(workflowStub::initiateOverDueJob, notificationEventType);

    String workflowId = execution.getWorkflowId();
    String runId = execution.getRunId();

    log.info(
        "Workflow triggered asynchronously. type={}, workflowId={}, runId={}",
        notificationEventType,
        workflowId,
        runId);

    // Persist reference
    /*     WorkflowReferenceEntity workflowReferenceEntity =
                  WorkflowReferenceEntity.builder()
                          .referenceNo(LocalDateTime.now().toString())
                          .workflowId(workflowId)
                          .runId(runId)
                          .type(WorkflowActionEnum.DUE_PAYMENT_NOTIFICATION.name())
                          .build();

          workflowClientRepository.save(workflowReferenceEntity);
    */
    // ✅ Immediate response (workflow still running)
    return OrchestratorResponse.builder()
        .isSuccess(true)
        .message("Workflow started successfully")
        .build();
    /*String workflowId = WorkflowStub.fromTyped(sellerPOWorkflow).getExecution().getWorkflowId();

    log.info(
        "Workflow triggered for notification type - {} workflow id -{}",
        notificationEventType,
        workflowId);
    ObjectMapper objectMapper = new ObjectMapper();

    WorkflowReferenceEntity workflowReferenceEntity =
        WorkflowReferenceEntity.builder()
            .referenceNo(LocalDateTime.now().toString())
            .workflowId(workflowId)
            .type(WorkflowActionEnum.DUE_PAYMENT_NOTIFICATION.name())
            .build();
    workflowClientRepository.save(workflowReferenceEntity);

    log.info("initialised workflow  with id {}", workflowId);*/

    /*
        SellerPOWorkflowInitiationResponse result =
            io.temporal.client.WorkflowStub.fromTyped(sellerPOWorkflow)
                .getResult(SellerPOWorkflowInitiationResponse.class);
    */

    //  return response;
  }
}
