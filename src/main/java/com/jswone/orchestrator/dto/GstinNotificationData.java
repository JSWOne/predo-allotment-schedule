package com.jswone.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GstinNotificationData {

  private String senderType;
  private NotificationConfig notificationConfig;

  @JsonProperty("notificationData")
  private LedgerDueNotificationDetails ledgerDueNotificationDetails;

  private List<String> channels;
  private String templateType;
}
