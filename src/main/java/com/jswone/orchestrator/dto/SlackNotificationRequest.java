package com.jswone.orchestrator.dto;

import java.util.List;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SlackNotificationRequest {

  private List<String> channels;
  private NotificationConfig notificationConfig;
  private NotificationData notificationData;
}
