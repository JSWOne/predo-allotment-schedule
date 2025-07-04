package com.jswone.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrchestratorResponse<T> {
  private Boolean isSuccess;
  private String message;
  private T data;
}
