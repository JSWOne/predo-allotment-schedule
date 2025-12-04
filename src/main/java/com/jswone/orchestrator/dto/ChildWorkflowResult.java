package com.jswone.orchestrator.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChildWorkflowResult {
  private String gstin;
  private boolean success;
  private String error;
  private String eventId;
}
