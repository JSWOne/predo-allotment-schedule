package com.jswone.orchestrator.config.pubsub;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class PubSubChannelConfig {

  @Bean
  public MessageChannel orchestratorToNotificationService() {
    return new DirectChannel();
  }
}
