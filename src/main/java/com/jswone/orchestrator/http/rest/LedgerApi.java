package com.jswone.orchestrator.http.rest;

import com.jswone.orchestrator.config.ExternalApi;
import com.jswone.orchestrator.dto.*;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class LedgerApi {

  @Value("${external-service.ledger.base-url}")
  private String ledgerBaseUrl;

  @Value("${external-service.ledger.client-user}")
  private String ledgerClientUser;

  @Value("${external-service.ledger.client-secret}")
  private String ledgerClientUserSecret;

  private final Environment environment;
  private final RestTemplate restTemplate;
  private final ExternalApi externalApi;

  public LedgerApi(Environment environment, RestTemplate restTemplate, ExternalApi externalApi) {
    this.environment = environment;
    this.restTemplate = restTemplate;
    this.externalApi = externalApi;
  }

  @PostConstruct
  private void setEnv() {
    this.ledgerBaseUrl = environment.getProperty("external-service.ledger.base-url");
    this.ledgerClientUser = environment.getProperty("external-service.ledger.client-user");
    this.ledgerClientUserSecret = environment.getProperty("external-service.ledger.client-secret");
  }

  private HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("client_id", this.ledgerClientUser);
    headers.set("Authorization", this.ledgerClientUserSecret);
    return headers;
  }

  private <T, R> R httpCall(String url, HttpMethod method, T requestBody, Class<R> responseType) {
    HttpEntity<T> httpEntity = new HttpEntity<>(requestBody, this.getHeaders());
    ResponseEntity<R> response = restTemplate.exchange(url, method, httpEntity, responseType);
    return response.getBody();
  }

  public LedgerGstinResponse fetchGstinsForDueNotification(
      NotificationEventType notificationEventType) {
    log.info(
        "Inside ledgerAPI for call ledger service for gstins list for notification {}",
        notificationEventType);

    String baseUrl =
        ledgerBaseUrl.concat(
            externalApi.getServices().get("ledger").get("fetch-notification-gstin"));

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(baseUrl)
            .queryParam("notificationEventType", notificationEventType);

    HttpEntity<Void> httpEntity = new HttpEntity<>(this.getHeaders());
    String url = builder.toUriString();
    return this.httpCall(url, HttpMethod.GET, httpEntity, LedgerGstinResponse.class);
  }

  public GstinNotificationDataResponse fetchGstinNotificationData(
      NotificationEventType notificationEventType, String gstin) {
    log.info("Inside ledgerAPI for call ledger service for notification data for gstin {}", gstin);
    String baseUrl =
        ledgerBaseUrl.concat(
            externalApi.getServices().get("ledger").get("fetch-gstin-notification-data"));

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(baseUrl)
            .queryParam("notificationEventType", notificationEventType)
            .queryParam("gstin", gstin);

    HttpEntity<Void> httpEntity = new HttpEntity<>(this.getHeaders());
    String url = builder.toUriString();
    return this.httpCall(url, HttpMethod.GET, httpEntity, GstinNotificationDataResponse.class);
  }

  public PostNotificationDataResponse postNotificationDataToLedger(
      PostNotificationDataRequest postNotificationDataRequest) {
    log.info("Calling ledger service to post  notification data");
    String baseUrl =
        ledgerBaseUrl.concat(
            externalApi.getServices().get("ledger").get("post-due-notification-data"));

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);

    HttpEntity<PostNotificationDataRequest> httpEntity =
        new HttpEntity<>(postNotificationDataRequest, this.getHeaders());
    String url = builder.toUriString();
    return this.httpCall(url, HttpMethod.POST, httpEntity, PostNotificationDataResponse.class);
  }

  public CSVDataForDueNotificationResponse fetchCSVDataForDueNotification(
      CSVDataForDueNotificationRequest csvDataForDueNotificationRequest) {
    log.info("Calling ledger service to fetchCSVDataForDueNotification");
    String baseUrl =
        ledgerBaseUrl.concat(
            externalApi.getServices().get("ledger").get("fetch-csv-notification-data"));

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);

    HttpEntity<CSVDataForDueNotificationRequest> httpEntity =
        new HttpEntity<>(csvDataForDueNotificationRequest, this.getHeaders());
    String url = builder.toUriString();
    return this.httpCall(url, HttpMethod.POST, httpEntity, CSVDataForDueNotificationResponse.class);
  }
}
