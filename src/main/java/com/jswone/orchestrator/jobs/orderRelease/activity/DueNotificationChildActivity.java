package com.jswone.orchestrator.jobs.orderRelease.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jswone.orchestrator.dto.GstinNotificationData;
import com.jswone.orchestrator.dto.GstinNotificationDataResponse;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface DueNotificationChildActivity {

  GstinNotificationDataResponse fetchGstinNotificationData(
      NotificationEventType notificationEventType, String gstin);

  void sendNotificationToGstin(GstinNotificationData gstinNotificationData, String gstin)
      throws JsonProcessingException;
}
