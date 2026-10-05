package com.jswone.orchestrator.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaleOpenSoResponse {

  private Boolean success;
  private List<StaleOpenSoListingItemDTO> data;
  private Integer totalCount;
  private Integer closedCount;
  private Integer abandonedCount;
  private List<Long> closedIds;
  private List<Long> abandonedIds;
}
