package com.jswone.orchestrator.dto;

import java.util.List;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmailTriggerNotificationRequest {

  private String senderType;
  private NotificationConfig notificationConfig;
  private PaymentEmailReportNotificationData notificationData;
  private List<String> channels;
  private String templateType;
}
