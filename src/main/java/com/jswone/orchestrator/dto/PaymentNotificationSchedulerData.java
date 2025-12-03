package com.jswone.orchestrator.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaymentNotificationSchedulerData {

  private Map<String, String> triggeredGstin;
  private Map<String, String> errorData;
}
