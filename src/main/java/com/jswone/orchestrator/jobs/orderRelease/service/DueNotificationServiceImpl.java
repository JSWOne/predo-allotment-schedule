package com.jswone.orchestrator.jobs.orderRelease.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jswone.orchestrator.dto.OrchestratorResponse;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import com.jswone.orchestrator.dto.enums.WorkflowActionEnum;
import com.jswone.orchestrator.jobs.orderRelease.workflow.DueNotificationWorkflow;
import com.jswone.orchestrator.persistence.entity.WorkflowReferenceEntity;
import com.jswone.orchestrator.persistence.repository.WorkFlowReferenceRepository;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DueNotificationServiceImpl implements DueNotificationService {

  private final WorkflowClient workflowClient;
  private final WorkFlowReferenceRepository workflowClientRepository;

  @Value("${temporal.due-notification-task-queue}")
  private String temporalDueNotificationTaskQueue;

  @Override
  public OrchestratorResponse initiateOverDueNotificationWorkflow(
      NotificationEventType notificationEventType) {
    log.info("Workflow to be triggered for confirming order {}");
    WorkflowOptions options =
        WorkflowOptions.newBuilder().setTaskQueue(temporalDueNotificationTaskQueue).build();
    DueNotificationWorkflow sellerPOWorkflow =
        workflowClient.newWorkflowStub(DueNotificationWorkflow.class, options);

    OrchestratorResponse response = sellerPOWorkflow.initiateOverDueJob(notificationEventType);

    // CreateOrderWorkflow wf = workflowClient.newWorkflowStub(CreateOrderWorkflow.class, options);
    /*
        WorkflowExecution exec =
            io.temporal.client.WorkflowClient.start(
                sellerPOWorkflow::initiateSellerPOJob, sellerPOWorkflowInitiationRequest);
    */

    String workflowId = WorkflowStub.fromTyped(sellerPOWorkflow).getExecution().getWorkflowId();

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

    log.info("initialised workflow  with id {}", workflowId);

    /*
        SellerPOWorkflowInitiationResponse result =
            io.temporal.client.WorkflowStub.fromTyped(sellerPOWorkflow)
                .getResult(SellerPOWorkflowInitiationResponse.class);
    */

    return response;
  }
}
