package com.jswone.orchestrator.jobs.orderRelease.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jswone.orchestrator.dto.OrderReleaseTemporalWorkflowRequest;
import com.jswone.orchestrator.entity.WorkflowReferenceEntity;
import com.jswone.orchestrator.enums.WorkflowActionEnum;
import com.jswone.orchestrator.jobs.orderRelease.workflow.OrderReleaseWorkflow;
import com.jswone.orchestrator.repository.WorkFlowReferenceRepository;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderReleaseServiceImpl implements OrderReleaseService {

  private final WorkflowClient workflowClient;
  private final WorkFlowReferenceRepository workflowClientRepository;

  @Value("${temporal.order-release-task-queue}")
  private String temporalOrderReleaseTaskQueue;

  @Override
  public void initiateOrderReleaseWorkflow(
      OrderReleaseTemporalWorkflowRequest orderReleaseTemporalWorkflowRequest) {
    log.info(
        "Workflow to be triggered for order {}",
        orderReleaseTemporalWorkflowRequest.getOrderNumber());
    WorkflowOptions options =
        WorkflowOptions.newBuilder().setTaskQueue(temporalOrderReleaseTaskQueue).build();
    OrderReleaseWorkflow orderReleaseWorkflow =
        workflowClient.newWorkflowStub(OrderReleaseWorkflow.class, options);
    WorkflowExecution execution =
        WorkflowClient.start(
            orderReleaseWorkflow::initiateOrderReleaseJob, orderReleaseTemporalWorkflowRequest);
    log.info(
        "Workflow triggered for - {} workflow id -{}",
        orderReleaseTemporalWorkflowRequest.getOrderNumber(),
        execution.getWorkflowId());
    ObjectMapper objectMapper = new ObjectMapper();

    WorkflowReferenceEntity workflowReferenceEntity =
        WorkflowReferenceEntity.builder()
            .referenceNo(orderReleaseTemporalWorkflowRequest.getOrderNumber())
            .workflowId(execution.getWorkflowId())
            .type(WorkflowActionEnum.ORDER_RELEASE.name())
            .metadata(objectMapper.convertValue(orderReleaseTemporalWorkflowRequest, Map.class))
            .build();
    workflowClientRepository.save(workflowReferenceEntity);

    log.info(
        "initialised workflow for order {} with id {}",
        orderReleaseTemporalWorkflowRequest.getOrderNumber(),
        execution.getWorkflowId());
  }
}
