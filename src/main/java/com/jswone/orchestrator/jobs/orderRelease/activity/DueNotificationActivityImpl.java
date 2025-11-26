package com.jswone.orchestrator.jobs.orderRelease.activity;

import com.jswone.orchestrator.dto.*;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import com.jswone.orchestrator.http.rest.JomsApi;
import com.jswone.orchestrator.http.rest.LedgerApi;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DueNotificationActivityImpl implements DueNotificationActivity {

  private final JomsApi jomsApi;

  private final LedgerApi ledgerApi;

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
}
