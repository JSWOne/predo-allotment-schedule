package com.jswone.orchestrator.config.pubsub;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.jswone.orchestrator.constants.PubSubChannels;
import com.jswone.orchestrator.enums.TopicName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;

@Configuration
@Slf4j
public class PubSubAdaptorConfiguration {

  @Value("${spring.cloud.gcp.project-id}")
  private String gcpTopic;

  @Value("${spring.cloud.gcp.pubsub.orchestrator-to-notification}")
  private String orchestratorToNotificationService;

  @Bean
  @ServiceActivator(inputChannel = PubSubChannels.ORCHESTRATOR_TO_NOTIFICATION_SERVICE)
  public MessageHandler orchestratorToNotificationServiceMessageSender(
      PubSubTemplate pubSubTemplate) {
    PubSubMessageHandler adapter =
        new PubSubMessageHandler(
            pubSubTemplate,
            String.format(
                TopicName.ORCHESTRATOR_TO_NOTIFICATION.getTopicName(),
                gcpTopic,
                orchestratorToNotificationService));
    adapter.setSuccessCallback(
        (ackId, message) -> log.info("Event Notification Message was sent successfully."));
    adapter.setFailureCallback(
        (ackId, message) -> log.info("Event Notification Message failed to sent."));
    return adapter;
  }
}
