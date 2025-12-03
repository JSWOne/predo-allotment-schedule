package com.jswone.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class GstinNotificationDataResponse {

  private boolean success;
  private String errorMessage;
  private GstinNotificationData data;
}
