package com.jswone.orchestrator.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDuePayments {
  private BigDecimal totalAmount = BigDecimal.ZERO;
  private List<PaymentDueDetails> details = new ArrayList<>();
}
