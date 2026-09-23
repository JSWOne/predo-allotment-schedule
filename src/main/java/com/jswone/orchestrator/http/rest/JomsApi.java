package com.jswone.orchestrator.http.rest;

import com.jswone.orchestrator.config.ExternalApi;
import com.jswone.orchestrator.dto.*;
import com.jswone.orchestrator.dto.StaleOpenSoResponse;
import com.jswone.orchestrator.dto.UpdateStaleOpenSoRequest;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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

  public StaleOpenSoResponse fetchStaleOpenSos() {
    log.info("Calling JOMS to fetch stale open SOs");
    String url =
        UriComponentsBuilder.fromHttpUrl(
                jomsBaseUrl.concat(externalApi.getServices().get("joms").get("stale-open-so")))
            .toUriString();

    StaleOpenSoResponse response =
        this.httpCall(url, HttpMethod.GET, null, StaleOpenSoResponse.class);

    if (response == null) {
      log.warn("Received null response from JOMS for stale-open-so");
      return null;
    }
    log.info("Response received from JOMS for stale open SO: total={}", response.getTotalCount());
    return response;
  }

  public Map<String, Integer> updateStaleOpenSos(List<Long> closedIds) {
    log.info("Calling JOMS to update stale open SOs with closedIds={}", closedIds);
    String url =
        UriComponentsBuilder.fromHttpUrl(
                jomsBaseUrl.concat(
                    externalApi.getServices().get("joms").get("update-stale-open-so")))
            .toUriString();

    UpdateStaleOpenSoRequest request =
        UpdateStaleOpenSoRequest.builder().closedIds(closedIds).build();
    HttpEntity<UpdateStaleOpenSoRequest> httpEntity = new HttpEntity<>(request, this.getHeaders());

    ResponseEntity<Map<String, Integer>> response =
        restTemplate.exchange(
            url, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<>() {});

    log.info("JOMS update stale open SO response: {}", response.getBody());
    return response.getBody();
  }
}
