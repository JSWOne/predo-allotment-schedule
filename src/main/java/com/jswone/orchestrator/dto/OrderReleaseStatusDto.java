package com.jswone.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderReleaseStatusDto {
  private String orderNumber;
  private String releaseBlockStatus;
  private String cashbackNoteStatus;
}
