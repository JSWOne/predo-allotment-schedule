package com.jswone.orchestrator.dto.enums;

public enum TopicName {
  ORCHESTRATOR_TO_NOTIFICATION("projects/%1$s/topics/%2$s", "projects/%1$s/subscriptions/%2$s-sub");

  private final String topicName;
  private final String subscriptionName;

  TopicName(String topicName, String subscriptionName) {
    this.topicName = topicName;
    this.subscriptionName = subscriptionName;
  }

  public String getTopicName() {
    return topicName;
  }

  public String getSubscriptionName() {
    return subscriptionName;
  }
}
