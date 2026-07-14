package com.jswone.orchestrator.http.rest;

import com.jswone.orchestrator.config.ExternalApi;
import com.jswone.orchestrator.dto.*;
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
public class JomsApi {

  @Value("${external-service.joms.base-url}")
  private String jomsBaseUrl;

  @Value("${external-service.joms.api-key}")
  private String jomsApiKey;

  private final Environment environment;
  private final RestTemplate restTemplate;
  private final ExternalApi externalApi;

  public JomsApi(Environment environment, RestTemplate restTemplate, ExternalApi externalApi) {
    this.environment = environment;
    this.restTemplate = restTemplate;
    this.externalApi = externalApi;
  }

  @PostConstruct
  private void setEnv() {
    this.jomsBaseUrl = environment.getProperty("external-service.joms.base-url");
    this.jomsApiKey = environment.getProperty("external-service.joms.api-key");
  }

  private HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("X-API-KEY", this.jomsApiKey);
    return headers;
  }

  private <T, R> R httpCall(String url, HttpMethod method, T requestBody, Class<R> responseType) {
    HttpEntity<T> httpEntity = new HttpEntity<>(requestBody, this.getHeaders());
    ResponseEntity<R> response = restTemplate.exchange(url, method, httpEntity, responseType);
    return response.getBody();
  }

  public EligibleFinishedGoodsResponse fetchEligibleFgUpdatesForPreDoAllotment() {
    log.info("Calling joms to fetch eligible FG updates for pre-do allotment");
    String url =
        UriComponentsBuilder.fromHttpUrl(
                jomsBaseUrl.concat(
                    externalApi.getServices().get("joms").get("fetch-pending-pre-do")))
            .toUriString();

    EligibleFinishedGoodsResponse response =
        this.httpCall(url, HttpMethod.GET, null, EligibleFinishedGoodsResponse.class);

    if (response == null) {
      log.warn("Received null response from joms for fetch-pending-pre-do");
      return null;
    }
    log.info(
        "Response received from joms: records={}",
        response.getRecords() == null ? 0 : response.getRecords().size());
    return response;
  }

  public AttachPreDoResponse processFgUpdateForPreDoAllotment(
      EligibleFinishedGoodsResponse.FinishedGoodsRecord finishedGoodsRecord) {
    log.info(
        "Calling Joms service to attach pre-do to FG update, fgUpdateId={}",
        finishedGoodsRecord.getId());
    AttachPreDoRequest attachPreDoRequest =
        AttachPreDoRequest.builder().finishedGoodsUpdateId(finishedGoodsRecord.getId()).build();
    String url =
        UriComponentsBuilder.fromHttpUrl(
                jomsBaseUrl.concat(externalApi.getServices().get("joms").get("attach-pre-do")))
            .toUriString();

    return this.httpCall(url, HttpMethod.POST, attachPreDoRequest, AttachPreDoResponse.class);
  }
}
