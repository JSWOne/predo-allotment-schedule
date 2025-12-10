package com.jswone.orchestrator.jobs.orderRelease.workflow;

import com.jswone.orchestrator.dto.ChildWorkflowResult;
import com.jswone.orchestrator.dto.OrchestratorResponse;
import com.jswone.orchestrator.dto.PaymentNotificationSchedulerData;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import com.jswone.orchestrator.jobs.orderRelease.activity.DueNotificationActivity;
import com.jswone.orchestrator.jobs.orderRelease.activity.DueNotificationChildActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

public class DueNotificationWorkflowImpl implements DueNotificationWorkflow {
  private static final Logger log = Workflow.getLogger(DueNotificationWorkflow.class);

  private final DueNotificationActivity activity =
      Workflow.newActivityStub(
          DueNotificationActivity.class,
          ActivityOptions.newBuilder()
              .setStartToCloseTimeout(Duration.ofSeconds(30))
              .setScheduleToStartTimeout(Duration.ofMinutes(2))
              .setScheduleToCloseTimeout(Duration.ofMinutes(3))
              .setRetryOptions(
                  RetryOptions.newBuilder()
                      .setInitialInterval(Duration.ofSeconds(2)) // wait 2s before first retry
                      .setBackoffCoefficient(2) // exponential backoff (2x)
                      .setMaximumAttempts(3) //  try up to 3 times
                      .build())
              .build());

  private final DueNotificationChildActivity childActivity =
      Workflow.newActivityStub(
          DueNotificationChildActivity.class,
          ActivityOptions.newBuilder()
              .setStartToCloseTimeout(Duration.ofSeconds(30))
              .setScheduleToStartTimeout(Duration.ofMinutes(2))
              .setScheduleToCloseTimeout(Duration.ofMinutes(3))
              .setRetryOptions(
                  RetryOptions.newBuilder()
                      .setInitialInterval(Duration.ofSeconds(2)) // wait 2s before first retry
                      .setBackoffCoefficient(2) // exponential backoff (2x)
                      .setMaximumAttempts(3) //  try up to 3 times
                      .build())
              .build());

  /*@Override
  public OrchestratorResponse initiateOverDueJob(NotificationEventType notificationEventType) {
    log.info("Initiating due payment due notification for type {}", notificationEventType);
    OrchestratorResponse response = OrchestratorResponse.builder().isSuccess(Boolean.TRUE).build();

    try {

      List<String> gstinsList = activity.fetchGstinsForNotification(notificationEventType);
      Map<String, String> errorData = new HashMap<>();
      Map<String, String> successData = new HashMap<>();

      if (gstinsList.isEmpty()) {
        response.setIsSuccess(Boolean.FALSE);
        response.setMessage("No gstins fetched for Notification type " + notificationEventType);
        return response;
      }
      log.info("Gstins fetched successfully");

      gstinsList.forEach(
          gstin -> {
            try {
              log.info(
                  "Initiating {} payments notification job for gstin {}",
                  notificationEventType,
                  gstin);
              GstinNotificationDataResponse notification =
                  activity.fetchGstinNotificationData(notificationEventType, gstin);

              if (!notification.isSuccess()) {
                log.info("Unable to fetch notification data {}", gstin);
                errorData.put(gstin, notification.getErrorMessage());
              } else {
                log.info(
                    "Sending notification for gstin {} for notification {}", gstin, notification);
                activity.sendNotificationToGstin(notification.getData(), gstin);
                successData.put(
                    gstin,
                    notification
                        .getData()
                        .getLedgerDueNotificationDetails()
                        .getNotificationPaymentDueOtherData()
                        .getEventId());
              }
            } catch (Exception e) {
              log.info("Exception occurred in triggering notification for gstin {}", gstin);
              errorData.put(gstin, e.getMessage());
            }
          });

      log.info(
          "Storing the notification data in database for notification type {}",
          notificationEventType);
      activity.storeNotificationDataInDB(notificationEventType, successData, errorData, gstinsList);
      log.info("Trigger teams notification for notification type {}", notificationEventType);
      activity.sendTeamsNotification(
          PaymentNotificationSchedulerData.builder()
              .triggeredGstin(successData)
              .errorData(errorData)
              .build(),
          notificationEventType);
      log.info("trigger CSV notification");
      byte[] csvData =
          activity.fetchCSVDataForSchedulerNotification(
              notificationEventType, errorData, successData);
      log.info("Send csv file notification for notification {}", notificationEventType);
      activity.sendPaymentDueNotificationReportEmail(csvData);
      log.info("Successfully executed notification scheduler for {}", notificationEventType);
    } catch (Exception e) {
      log.info(
          "Exception occurred while initiating notification for type {}", notificationEventType);
      response.setIsSuccess(Boolean.FALSE);
      response.setMessage(e.toString());
      return response;
    }
    return response;
  }*/

  @Override
  public OrchestratorResponse initiateOverDueJob(NotificationEventType notificationEventType) {

    OrchestratorResponse response = OrchestratorResponse.builder().isSuccess(true).build();

    try {
      List<String> gstinsList = activity.fetchGstinsForNotification(notificationEventType);

      if (gstinsList.isEmpty()) {
        response.setIsSuccess(false);
        response.setMessage("No gstins fetched for Notification type " + notificationEventType);
        return response;
      }

      Map<String, String> successData = new HashMap<>();
      Map<String, String> errorData = new HashMap<>();

      // ============================
      // 🚀 PARALLEL CHILD WORKFLOWS
      // ============================

      List<Promise<ChildWorkflowResult>> promises =
          gstinsList.stream()
              .map(
                  gstin -> {
                    GstinNotificationChildWorkflow child =
                        Workflow.newChildWorkflowStub(GstinNotificationChildWorkflow.class);
                    return Async.function(child::processGstin, notificationEventType, gstin);
                  })
              .toList();

      // Wait for all child results
      for (Promise<ChildWorkflowResult> p : promises) {
        ChildWorkflowResult r = p.get();

        if (r.isSuccess()) {
          successData.put(r.getGstin(), r.getEventId());
        } else {
          errorData.put(r.getGstin(), r.getError());
        }
      }

      // Continue original logic
      activity.storeNotificationDataInDB(notificationEventType, successData, errorData, gstinsList);

      activity.sendTeamsNotification(
          PaymentNotificationSchedulerData.builder()
              .triggeredGstin(successData)
              .errorData(errorData)
              .build(),
          notificationEventType);

      byte[] csvData =
          activity.fetchCSVDataForSchedulerNotification(
              notificationEventType, errorData, successData);

      activity.sendPaymentDueNotificationReportEmail(csvData);

      return response;

    } catch (Exception e) {
      response.setIsSuccess(false);
      response.setMessage(e.toString());
      return response;
    }
  }
}
