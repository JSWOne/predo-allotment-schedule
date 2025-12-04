package com.jswone.orchestrator.jobs.orderRelease.workflow;

import com.jswone.orchestrator.dto.ChildWorkflowResult;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface GstinNotificationChildWorkflow {

  @WorkflowMethod
  ChildWorkflowResult processGstin(NotificationEventType type, String gstin);
}
