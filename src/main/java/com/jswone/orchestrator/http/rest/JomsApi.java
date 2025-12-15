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

  public CustomerPendingPreDoResponse fetchPendingPrePO(String gstin, Integer dueInDays) {
    log.info("Calling joms to fetch pending pre-do  {}", gstin);
    String baseUrl =
        jomsBaseUrl
            .concat(externalApi.getServices().get("joms").get("fetch-pending-pre-do"))
            .concat("/" + gstin);

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(baseUrl).queryParam("dueInDays", dueInDays);

    HttpEntity<Void> httpEntity = new HttpEntity<>(this.getHeaders());
    String url = builder.toUriString();
    CustomerPendingPreDoResponse response =
        this.httpCall(url, HttpMethod.GET, httpEntity, CustomerPendingPreDoResponse.class);

    log.info("Response received from joms {}", response.toString());
    return response;
  }
}
