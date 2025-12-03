package com.jswone.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationPaymentDueOtherData {
  private String makePaymentUrl;

  @JsonProperty("advanceRequiredData")
  private List<AdvancesRequiredNotificationData> advancesRequiredNotificationData;

  @JsonProperty("dueLaterInvoicesData")
  private List<AccountReceivablesNotificationData> accountReceivablesNotDueNotificationData;

  @JsonProperty("dueInvoicesData")
  private List<AccountReceivablesNotificationData> accountReceivablesOverDueNotificationData;

  private BigDecimal netPaymentsDue;
  private BigDecimal totalPaymentsDue;
  private BigDecimal usableLedgerBalance;
  private BigDecimal advanceRequiredAmount;
  private BigDecimal accountsReceivablesDue;
  private BigDecimal totalDueLater;
  private BigDecimal dueAmount;
  private String eventId;
}
