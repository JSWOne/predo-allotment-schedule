package com.jswone.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerPendingPreDoResponse {
  private Boolean success;
  private String requestId;
  private CustomerDuePayments data;
  private ErrorResponse error;
}
