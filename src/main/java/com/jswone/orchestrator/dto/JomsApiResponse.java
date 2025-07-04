package com.jswone.orchestrator.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class JomsApiResponse {
  private Boolean isSuccess;
  private String message;
}
