package com.jswone.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jswone.orchestrator.enums.OrderPaymentTransactionEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderReleaseTemporalWorkflowRequest {
  private String customerId;
  private String orderNumber;

  private String releaseReason;

  private String shortClosedDetails;
  private String dispatchNumber;
  private Boolean isRefundNeeded;
  private String advanceAmountToBeRelease;
  private Boolean isOmsCall;
  private String creditLimitId;
  private String creditAmountToBeReleased;
  private OrderPaymentTransactionEnum orderPaymentTransactionEnum;
}
