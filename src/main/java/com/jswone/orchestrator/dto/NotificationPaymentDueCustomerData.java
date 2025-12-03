package com.jswone.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationPaymentDueCustomerData {
  private String name;
  private String beneficiaryName;

  @JsonProperty("beneficiaryAccountNum")
  private String beneficiaryAccountNumber;

  private String ifscCode;
}
