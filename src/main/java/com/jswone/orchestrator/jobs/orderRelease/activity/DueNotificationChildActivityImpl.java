package com.jswone.orchestrator.jobs.orderRelease.activity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jswone.orchestrator.config.pubsub.PubSubGateway;
import com.jswone.orchestrator.dto.AdvancesRequiredNotificationData;
import com.jswone.orchestrator.dto.CustomerPendingPreDoResponse;
import com.jswone.orchestrator.dto.GstinNotificationData;
import com.jswone.orchestrator.dto.GstinNotificationDataResponse;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import com.jswone.orchestrator.http.rest.JomsApi;
import com.jswone.orchestrator.http.rest.LedgerApi;
import io.temporal.failure.ApplicationFailure;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    log.info("triggering notification for gstin {} event id {}", gstin,gstinNotificationData
            .getLedgerDueNotificationDetails()
            .getNotificationPaymentDueOtherData()
            .getEventId());
    Message<String> message =
        MessageBuilder.createMessage(
            objectMapper.writeValueAsString(gstinNotificationData), new MessageHeaders(null));
    pubSubGateway.sendMessageToNotificationService(message);

    log.info("Successfully sent notification for gstin {} ", gstin);
  }

  @Override
  public GstinNotificationDataResponse populatePendingPreDoData(
      NotificationEventType notificationEventType,
      String gstin,
      GstinNotificationDataResponse gstinNotificationDataResponse) {
    log.info(
        "Fetching pending  pre do for gstin {} from joms for sending notification {} ",
        gstin,
        notificationEventType);
    int dueInDays =
        switch (notificationEventType) {
          case DUE_IN_5_DAYS -> 5;
          case OVER_DUE -> 0;
          case DUE_TODAY -> 1;
        };
    List<AdvancesRequiredNotificationData> predoList = new ArrayList<>();
    log.info("Calling joms to fetch pending pre-do");
    CustomerPendingPreDoResponse response = jomsApi.fetchPendingPrePO(gstin, dueInDays);
    if (Objects.isNull(response) || !response.getSuccess()) {
      throw ApplicationFailure.newFailure(
          "fetch pre do data failed for gstin " + gstin, "DUE_PRE_DO_FETCH_FAILED", false);
    }
    if (!Objects.isNull(response.getData())) {
      if (!response.getData().getDetails().isEmpty()) {
        log.info("Populating pre-do data");
        response
            .getData()
            .getDetails()
            .forEach(
                preDo -> {
                  if (!Objects.isNull(
                      gstinNotificationDataResponse.getData().getLedgerDueNotificationDetails())) {
                    gstinNotificationDataResponse
                        .getData()
                        .getLedgerDueNotificationDetails()
                        .getNotificationPaymentDueOtherData()
                        .getAdvancesRequiredNotificationData()
                        .add(
                            AdvancesRequiredNotificationData.builder()
                                .dueDate(preDo.getDueDate())
                                .orderNumber(preDo.getOrderNumber())
                                .pendingAmount(preDo.getAmount())
                                .type("For material dispatch")
                                .build());

                      BigDecimal updatedAdvanceAmount =gstinNotificationDataResponse
                              .getData()
                              .getLedgerDueNotificationDetails()
                              .getNotificationPaymentDueOtherData().getAdvanceRequiredAmount().add(preDo.getAmount());
                      gstinNotificationDataResponse
                              .getData()
                              .getLedgerDueNotificationDetails()
                              .getNotificationPaymentDueOtherData().setAdvanceRequiredAmount(updatedAdvanceAmount);

                      BigDecimal updatedTotalPayment = gstinNotificationDataResponse
                              .getData()
                              .getLedgerDueNotificationDetails()
                              .getNotificationPaymentDueOtherData().getTotalPaymentsDue().add(preDo.getAmount());
                      gstinNotificationDataResponse
                              .getData()
                              .getLedgerDueNotificationDetails()
                              .getNotificationPaymentDueOtherData().setTotalPaymentsDue(updatedTotalPayment);

                      BigDecimal updatedNetPayment = gstinNotificationDataResponse
                              .getData()
                              .getLedgerDueNotificationDetails()
                              .getNotificationPaymentDueOtherData().getNetPaymentsDue().add(preDo.getAmount());

                      gstinNotificationDataResponse
                              .getData()
                              .getLedgerDueNotificationDetails()
                              .getNotificationPaymentDueOtherData().setNetPaymentsDue(updatedNetPayment);

                  }
                });
      }
    } else {
      log.info("No pre do datafetched from joms");
    }

    return gstinNotificationDataResponse;
  }
}
