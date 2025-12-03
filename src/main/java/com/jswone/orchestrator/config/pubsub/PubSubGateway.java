package com.jswone.orchestrator.config.pubsub;

import com.jswone.orchestrator.dto.PubSubRequest;
import com.jswone.orchestrator.dto.constants.PubSubChannels;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

@MessagingGateway
public interface PubSubGateway {

  @Gateway(requestChannel = PubSubChannels.ORCHESTRATOR_TO_NOTIFICATION_SERVICE)
  void sendMessageFoNotificationService(Message<PubSubRequest> message);

  @Gateway(requestChannel = PubSubChannels.ORCHESTRATOR_TO_NOTIFICATION_SERVICE)
  <T> void sendMessageToNotificationService(Message<T> payload);
}
