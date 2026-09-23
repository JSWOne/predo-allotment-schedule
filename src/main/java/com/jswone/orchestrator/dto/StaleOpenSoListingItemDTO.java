package com.jswone.orchestrator.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaleOpenSoListingItemDTO {

  private Long id;
  private String salesOrder;
  private String soItem;
  private String plant;
  private String uploadId;
  private String currentStatus;
  private BigDecimal orderedQty;
  private BigDecimal invoicedQty;
  private String pendingAction;
}
