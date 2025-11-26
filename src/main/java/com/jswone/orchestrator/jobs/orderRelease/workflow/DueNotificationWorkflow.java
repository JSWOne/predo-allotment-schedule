package com.jswone.orchestrator.jobs.orderRelease.workflow;

import com.jswone.orchestrator.dto.OrchestratorResponse;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface DueNotificationWorkflow {
  @WorkflowMethod
  OrchestratorResponse initiateOverDueJob(NotificationEventType notificationEventType);
}
