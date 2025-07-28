package com.jswone.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderReleaseStatusRequest {
  private String orderNumber;
  private String releaseStatus;
  private String cashbackStatus;
}
