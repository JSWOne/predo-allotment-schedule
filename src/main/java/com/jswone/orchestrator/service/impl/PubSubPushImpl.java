package com.jswone.orchestrator.service.impl;

import com.jswone.orchestrator.config.pubsub.PubSubGateway;
import com.jswone.orchestrator.constants.ServiceConstants;
import com.jswone.orchestrator.dto.PubSubRequest;
import com.jswone.orchestrator.service.PubSubPush;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PubSubPushImpl implements PubSubPush {

  private final PubSubGateway pubSubGateway;

  @Async
  @Override
  public void pushToNotificationQueue(PubSubRequest pubSubRequest) {
    try {
      String eventId = String.valueOf(UUID.randomUUID());
      Map<String, Object> headers = new HashMap<>();
      headers.put(ServiceConstants.EVENT_ID, eventId);
      MessageHeaders messageHeaders = new MessageHeaders(headers);
      Message<PubSubRequest> message = MessageBuilder.createMessage(pubSubRequest, messageHeaders);
      pubSubGateway.sendMessageFoNotificationService(message);
    } catch (Exception ex) {
      log.info("Failure in publishing details to notification queue {}", ex);
    }
  }
}
