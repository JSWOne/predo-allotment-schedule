package com.jswone.orchestrator.dto;

import com.jswone.orchestrator.dto.enums.FinishedGoodsStateEnum;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibleFinishedGoodsResponse {

  private String requestId;
  private List<FinishedGoodsRecord> records;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FinishedGoodsRecord {
    private Long id;
    private Long lineItemId;
    private Long customerOrderId;
    private Long sellerOrderId;
    private BigDecimal allocatedQty;
    private Long readinessDate;
    private String variantMMId;
    private FinishedGoodsStateEnum fgReadinessState;
    private String sellerOrderRef;
    private String sellerOlRef;
    private Long parentId;
    private Long createdAt;
    private Long updatedAt;
    private String createdBy;
    private Long doId;
  }
}
