package com.jswone.orchestrator.jobs.orderRelease.workflow;

import com.jswone.orchestrator.dto.OrchestratorResponse;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import com.jswone.orchestrator.jobs.orderRelease.activity.DueNotificationActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.List;
import org.slf4j.Logger;

public class DueNotificationWorkflowImpl implements DueNotificationWorkflow {
  private static final Logger log = Workflow.getLogger(DueNotificationWorkflow.class);

  DueNotificationActivity loopActivityStub =
      Workflow.newActivityStub(
          DueNotificationActivity.class,
          ActivityOptions.newBuilder()
              .setRetryOptions(
                  RetryOptions.newBuilder()
                      .setInitialInterval(Duration.ofMinutes(10))
                      .setBackoffCoefficient(1.0)
                      .build())
              .setStartToCloseTimeout(Duration.ofSeconds(30))
              .build());

  DueNotificationActivity retryLimitedStub =
      Workflow.newActivityStub(
          DueNotificationActivity.class,
          ActivityOptions.newBuilder()
              .setRetryOptions(
                  RetryOptions.newBuilder()
                      .setInitialInterval(Duration.ofSeconds(2))
                      .setBackoffCoefficient(2)
                      .setMaximumAttempts(5)
                      .setMaximumInterval(Duration.ofSeconds(60))
                      .build())
              .setStartToCloseTimeout(Duration.ofSeconds(60))
              .build());

  @Override
  public OrchestratorResponse initiateOverDueJob(NotificationEventType notificationEventType) {
    log.info("Initiating due payment due notification for type {}", notificationEventType);
    OrchestratorResponse response = OrchestratorResponse.builder().isSuccess(Boolean.TRUE).build();

    try {

      List<String> gstinsList = retryLimitedStub.fetchGstinsForNotification(notificationEventType);

      if (gstinsList.isEmpty()) {
        response.setIsSuccess(Boolean.FALSE);
        response.setMessage("No gstins fetched for Notification type " + notificationEventType);
        return response;
      }
      log.info("Gstins fetched successfully");

    } catch (Exception e) {
      log.info(
          "Exception occurred while initiating notification for type {}", notificationEventType);
      response.setIsSuccess(Boolean.FALSE);
      response.setMessage(e.toString());
      return response;
    }
    return response;
  }
}
