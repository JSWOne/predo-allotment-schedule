package com.jswone.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import com.jswone.request.ServiceRequest;
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
public class PostNotificationDataRequest  {
  @JsonProperty("gstins")
  private List<String> gstins;

  private NotificationEventType notificationEventType;
  private Map<String, String> successData;
  private Map<String, String> errorData;
}
