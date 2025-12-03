package com.jswone.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AccountReceivablesNotificationData {

  @JsonProperty("type")
  private String documentType;

  @JsonProperty("id")
  private String documentId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
  private LocalDate dueDate;

  private BigDecimal pendingAmount;
  private LocalDate invoiceDueDate;
  private Long pendingDays;
  private String gstin;
}
