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
public class LedgerDueNotificationDetails {
  @JsonProperty("customerData")
  private NotificationPaymentDueCustomerData notificationPaymentDueCustomerData;

  @JsonProperty("otherData")
  private NotificationPaymentDueOtherData notificationPaymentDueOtherData;
}
