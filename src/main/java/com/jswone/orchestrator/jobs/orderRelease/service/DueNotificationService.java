package com.jswone.orchestrator.jobs.orderRelease.service;

import com.jswone.orchestrator.dto.OrchestratorResponse;
import com.jswone.orchestrator.dto.enums.NotificationEventType;

public interface DueNotificationService {

  OrchestratorResponse initiateOverDueNotificationWorkflow(
      NotificationEventType notificationEventType);
}
