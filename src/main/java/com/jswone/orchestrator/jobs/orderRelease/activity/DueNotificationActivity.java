package com.jswone.orchestrator.jobs.orderRelease.activity;

import com.jswone.orchestrator.dto.PaymentNotificationSchedulerData;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import io.temporal.activity.ActivityInterface;
import java.util.List;
import java.util.Map;

@ActivityInterface
public interface DueNotificationActivity {

  List<String> fetchGstinsForNotification(NotificationEventType notificationEventType);

  /*
    GstinNotificationDataResponse fetchGstinNotificationData(
        NotificationEventType notificationEventType, String gstin);

    void sendNotificationToGstin(GstinNotificationData gstinNotificationData, String gstin)
        throws JsonProcessingException;
  */

  void storeNotificationDataInDB(
      NotificationEventType notificationEventType,
      Map<String, String> successData,
      Map<String, String> errorData,
      List<String> gstinsList);

  void sendTeamsNotification(
      PaymentNotificationSchedulerData paymentNotificationSchedulerData,
      NotificationEventType notificationEventType);

  byte[] fetchCSVDataForSchedulerNotification(
      NotificationEventType notificationEventType,
      Map<String, String> errorData,
      Map<String, String> successData);

  void sendPaymentDueNotificationReportEmail(byte[] csvData);
}
