package com.jswone.orchestrator.dto;

import java.util.Map;

public class PubSubMessage {
  private String eventId;
  private String eventType;
  private Map<String, Object> payload;
}
