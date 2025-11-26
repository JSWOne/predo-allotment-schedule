package com.jswone.orchestrator.jobs.orderRelease.activity;

import com.jswone.orchestrator.dto.enums.NotificationEventType;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.List;

@ActivityInterface
public interface DueNotificationActivity {

  @ActivityMethod
  List<String> fetchGstinsForNotification(NotificationEventType notificationEventType);
}
