package com.jswone.orchestrator.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDueDetails {
  private String type;
  private String documentNumber;
  private String orderNumber;
  private BigDecimal amount = BigDecimal.ZERO;
  private LocalDate dueDate;
  private Long overdueDays = 0L;
  private String reason;
}
