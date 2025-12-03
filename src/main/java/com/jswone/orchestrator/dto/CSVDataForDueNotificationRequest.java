package com.jswone.orchestrator.dto;

import com.jswone.orchestrator.dto.enums.NotificationEventType;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CSVDataForDueNotificationRequest {
  private NotificationEventType notificationEventType;
  private Map<String, String> errorData;
  private Map<String, String> successData;
}
