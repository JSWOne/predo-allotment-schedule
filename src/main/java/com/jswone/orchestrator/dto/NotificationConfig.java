package com.jswone.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationConfig {
  private String to;
  private String cc;
  private String bcc;
  private String subject;
  private String mobileNumber;
  private String channel;
}
