package com.jswone.orchestrator.service;

import com.jswone.orchestrator.dto.PubSubRequest;

public interface PubSubPush {

  void pushToNotificationQueue(PubSubRequest pubSubRequest);
}
