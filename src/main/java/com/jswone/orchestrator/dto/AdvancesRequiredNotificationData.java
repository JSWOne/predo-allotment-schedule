package com.jswone.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class AdvancesRequiredNotificationData {
  private String type;
  private String orderNumber;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
  private LocalDate dueDate;

  private BigDecimal pendingAmount;
}
