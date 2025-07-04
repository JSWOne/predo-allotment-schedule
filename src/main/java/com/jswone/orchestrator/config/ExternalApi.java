package com.jswone.orchestrator.config;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "services")
public class ExternalApi {

  private Map<String, Map<String, String>> services;

  public Map<String, Map<String, String>> getServices() {
    return services;
  }

  public void setServices(Map<String, Map<String, String>> services) {
    this.services = services;
  }
}
