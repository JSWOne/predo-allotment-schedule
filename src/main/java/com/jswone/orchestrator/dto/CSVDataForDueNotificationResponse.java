package com.jswone.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CSVDataForDueNotificationResponse {
  private Boolean success;
  private String errorMessage;
  private byte[] data;
}
