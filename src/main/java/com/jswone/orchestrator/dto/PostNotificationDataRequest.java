package com.jswone.orchestrator.dto;

import com.jswone.orchestrator.dto.enums.NotificationEventType;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostNotificationDataRequest {
  private List<String> gstins;
  private NotificationEventType notificationEventType;
  private Map<String, String> successData;
  private Map<String, String> errorData;
}
