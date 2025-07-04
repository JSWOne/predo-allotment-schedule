package com.jswone.orchestrator.jobs.orderRelease.workflow;

import com.jswone.orchestrator.dto.OrderReleaseTemporalWorkflowRequest;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderReleaseWorkflow {
  @WorkflowMethod
  void initiateOrderReleaseJob(
      OrderReleaseTemporalWorkflowRequest orderReleaseTemporalWorkflowRequest);
}
