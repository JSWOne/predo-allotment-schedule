package com.jswone.orchestrator.jobs.orderRelease.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jswone.orchestrator.config.pubsub.PubSubGateway;
import com.jswone.orchestrator.dto.GstinNotificationData;
import com.jswone.orchestrator.dto.GstinNotificationDataResponse;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import com.jswone.orchestrator.http.rest.JomsApi;
import com.jswone.orchestrator.http.rest.LedgerApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DueNotificationChildActivityImpl implements DueNotificationChildActivity {

  private final JomsApi jomsApi;

  @Value("${notification.report-to-email}")
  private String ledgerReportToEmail;

  @Value("${notification.report-cc-email}")
  private String ledgerReportCCEmail;

  @Autowired private PubSubGateway pubSubGateway;

  private final LedgerApi ledgerApi;
  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  public DueNotificationChildActivityImpl(JomsApi jomsApi, LedgerApi ledgerApi) {
    this.jomsApi = jomsApi;
    this.ledgerApi = ledgerApi;
  }

  @Override
  public GstinNotificationDataResponse fetchGstinNotificationData(
      NotificationEventType notificationEventType, String gstin) {
    log.info(
        "Fetching notification data for gstin {} from ledger for sending notification {} ",
        gstin,
        notificationEventType);

    return ledgerApi.fetchGstinNotificationData(notificationEventType, gstin);
  }

  @Override
  public void sendNotificationToGstin(GstinNotificationData gstinNotificationData, String gstin)
      throws JsonProcessingException {

    log.info("triggering notification for gstin {}", gstin);
    Message<String> message =
        MessageBuilder.createMessage(
            objectMapper.writeValueAsString(gstinNotificationData), new MessageHeaders(null));
    pubSubGateway.sendMessageToNotificationService(message);

    log.info("Successfully sent notification for gstin {} ", gstin);
  }
}
