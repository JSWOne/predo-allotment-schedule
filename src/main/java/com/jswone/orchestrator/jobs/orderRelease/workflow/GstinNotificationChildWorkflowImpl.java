package com.jswone.orchestrator.jobs.orderRelease.workflow;

import com.jswone.orchestrator.dto.ChildWorkflowResult;
import com.jswone.orchestrator.dto.GstinNotificationDataResponse;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import com.jswone.orchestrator.jobs.orderRelease.activity.DueNotificationChildActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import org.apache.commons.lang3.StringUtils;

public class GstinNotificationChildWorkflowImpl implements GstinNotificationChildWorkflow {
  private final DueNotificationChildActivity childActivity =
      Workflow.newActivityStub(
          DueNotificationChildActivity.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofMinutes(2)).build());

  @Override
  public ChildWorkflowResult processGstin(NotificationEventType type, String gstin) {

    try {
      if (!StringUtils.isEmpty(gstin)) {
        GstinNotificationDataResponse notification =
            childActivity.fetchGstinNotificationData(type, gstin);
        if (!notification.isSuccess()) {
          return ChildWorkflowResult.builder()
              .gstin(gstin)
              .success(false)
              .error(notification.getErrorMessage())
              .build();
        }
        notification = childActivity.populatePendingPreDoData(type, gstin, notification);
        if (!notification.isSuccess()) {
          return ChildWorkflowResult.builder()
              .gstin(gstin)
              .success(false)
              .error(notification.getErrorMessage())
              .build();
        }

        childActivity.sendNotificationToGstin(notification.getData(), gstin);

        String eventId =
            notification
                .getData()
                .getLedgerDueNotificationDetails()
                .getNotificationPaymentDueOtherData()
                .getEventId();

        return ChildWorkflowResult.builder().gstin(gstin).success(true).eventId(eventId).build();
      }
      return ChildWorkflowResult.builder().gstin(gstin).success(true).eventId("").build();
    } catch (Exception e) {
      return ChildWorkflowResult.builder()
          .gstin(gstin)
          .success(false)
          .error(e.getMessage())
          .build();
    }
  }
}
