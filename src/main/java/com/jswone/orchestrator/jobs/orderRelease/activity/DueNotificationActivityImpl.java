package com.jswone.orchestrator.jobs.orderRelease.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jswone.orchestrator.config.pubsub.PubSubGateway;
import com.jswone.orchestrator.dto.*;
import com.jswone.orchestrator.dto.constants.NotificationConstants;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import com.jswone.orchestrator.http.rest.JomsApi;
import com.jswone.orchestrator.http.rest.LedgerApi;
import io.temporal.failure.ApplicationFailure;
import java.time.LocalDateTime;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.threeten.bp.LocalDate;

@Slf4j
@Component
public class DueNotificationActivityImpl implements DueNotificationActivity {

  private final JomsApi jomsApi;

  @Value("${notification.report-to-email}")
  private String ledgerReportToEmail;

  @Value("${notification.report-cc-email}")
  private String ledgerReportCCEmail;

  @Autowired private PubSubGateway pubSubGateway;

  private final LedgerApi ledgerApi;
  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  public DueNotificationActivityImpl(JomsApi jomsApi, LedgerApi ledgerApi) {
    this.jomsApi = jomsApi;
    this.ledgerApi = ledgerApi;
  }

  public List<String> fetchGstinsForNotification(NotificationEventType notificationEventType) {
    log.info(
        "Fetching list of gstins from ledger for sending notification {}", notificationEventType);
    LedgerGstinResponse response = ledgerApi.fetchGstinsForDueNotification(notificationEventType);

    if (!Objects.isNull(response) && !response.getGstin().isEmpty()) {
      log.info("Gstin fetched from ledger service");
      return response.getGstin();
    }
    log.info("No gstin fetched from ledger service for notification : {}", notificationEventType);
    return List.of();
  }

  public GstinNotificationDataResponse fetchGstinNotificationData(
      NotificationEventType notificationEventType, String gstin) {
    log.info(
        "Fetching notification data for gstin {} from ledger for sending notification {} ",
        gstin,
        notificationEventType);

    return ledgerApi.fetchGstinNotificationData(notificationEventType, gstin);
  }

  public void sendNotificationToGstin(GstinNotificationData gstinNotificationData, String gstin)
      throws JsonProcessingException {

    log.info("triggering notification for gstin {}", gstin);
    Message<String> message =
        MessageBuilder.createMessage(
            objectMapper.writeValueAsString(gstinNotificationData), new MessageHeaders(null));
    pubSubGateway.sendMessageToNotificationService(message);

    log.info("Successfully sent notification for gstin {} ", gstin);
  }

  @Override
  public void storeNotificationDataInDB(
      NotificationEventType notificationEventType,
      Map<String, String> successData,
      Map<String, String> errorData,
      List<String> gstinsList) {
    log.info(
        "Inside method storeNotificationDataInDB to store notification  data in DB {}",
        notificationEventType);

    PostNotificationDataRequest postRequest =
        PostNotificationDataRequest.builder()
            .gstins(gstinsList)
            .errorData(errorData)
            .successData(successData)
            .notificationEventType(notificationEventType)
            .build();
    PostNotificationDataResponse response = ledgerApi.postNotificationDataToLedger(postRequest);
    log.info("Response for posting due notification data {}", response);
  }

  @Override
  public void sendTeamsNotification(
      PaymentNotificationSchedulerData paymentNotificationSchedulerData,
      NotificationEventType eventType) {
    log.info("Sending Slack notification data for eventType {}", eventType);
    String template =
        """
                            Successfully triggered payment notification for event %s at %s with below data
                             Successfully Triggered gstin : %s\s
                             Failed Data : %s""";

    String failedData = null;
    try {
      failedData = objectMapper.writeValueAsString(paymentNotificationSchedulerData.getErrorData());
    } catch (JsonProcessingException ex) {
      log.error("Error while converting failed data to string");
    }

    try {
      SlackNotificationRequest teamsNotification =
          SlackNotificationRequest.builder()
              .channels(List.of("TEAMS"))
              .notificationData(
                  NotificationData.builder()
                      .message(
                          String.format(
                              template,
                              eventType,
                              LocalDate.now(),
                              Optional.ofNullable(
                                      paymentNotificationSchedulerData.getTriggeredGstin())
                                  .map(Map::keySet)
                                  .orElse(null),
                              failedData))
                      .build())
              .build();

      Message<String> message =
          MessageBuilder.createMessage(
              objectMapper.writeValueAsString(teamsNotification), new MessageHeaders(null));
      pubSubGateway.sendMessageToNotificationService(message);

    } catch (Exception e) {
      log.error("Error while sending Slack notification for eventType {}", eventType, e);
    }
  }

  @Override
  public byte[] fetchCSVDataForSchedulerNotification(
      NotificationEventType notificationEventType,
      Map<String, String> errorData,
      Map<String, String> successData) {
    log.info("Fetching csv data from ledger for notification {}", notificationEventType);
    CSVDataForDueNotificationRequest request =
        CSVDataForDueNotificationRequest.builder()
            .errorData(errorData)
            .successData(successData)
            .notificationEventType(notificationEventType)
            .build();

    CSVDataForDueNotificationResponse response = ledgerApi.fetchCSVDataForDueNotification(request);

    log.info("Response for fetching csv data is {}", response.getSuccess());

    if (response.getSuccess() && !Objects.isNull(response.getData())) {
      log.info("successfully fetched csv data ");
      return response.getData();
    } else {
      log.info("no CSV data fetched to sent to notification report email");
      return null;
    }
  }

  @Override
  public void sendPaymentDueNotificationReportEmail(byte[] csvData) {

    if (Objects.isNull(csvData)) {
      throw ApplicationFailure.newFailure(
          "sendPaymentDueNotificationReportEmail failed", "DUE_NOTIFICATION_REPORT_FAILED", false);
    }
    LocalDate todayDate = LocalDate.now();
    try {
      EmailTriggerNotificationRequest emailTriggerNotificationRequest =
          EmailTriggerNotificationRequest.builder()
              .notificationData(
                  PaymentEmailReportNotificationData.builder()
                      .csvData(Arrays.toString(csvData))
                      .message(
                          String.format(
                              NotificationConstants.PAYMENT_NOTIFICATION_REPORT_MESSAGE_BODY,
                              todayDate))
                      .fileName(
                          NotificationConstants.PAYMENT_NOTIFICATION_REPORT_FILE_NAME.concat(
                              LocalDateTime.now().toString()))
                      .build())
              .channels(List.of(NotificationConstants.EMAIL_CHANNEL))
              .templateType(NotificationConstants.PAYMENT_NOTIFICATION_REPORT_TEMPLATE)
              .senderType(NotificationConstants.PAYMENT_SENDER_TYPE)
              .notificationConfig(
                  NotificationConfig.builder()
                      .to(ledgerReportToEmail)
                      .cc(ledgerReportCCEmail)
                      .subject(
                          String.format(
                              NotificationConstants.PAYMENT_NOTIFICATION_REPORT_SUBJECT,
                              LocalDate.now()))
                      .build())
              .build();

      Message<String> message =
          MessageBuilder.createMessage(
              objectMapper.writeValueAsString(emailTriggerNotificationRequest),
              new MessageHeaders(null));

      pubSubGateway.sendMessageToNotificationService(message);
      log.info("Payment due notification email message sent via PubSub successfully.");
    } catch (Exception ex) {
      log.error(
          "Error while sending payment due notification email via PubSub: {}", ex.getMessage(), ex);
    }
  }
}
