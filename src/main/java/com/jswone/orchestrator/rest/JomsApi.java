package com.jswone.orchestrator.rest;

import com.jswone.orchestrator.config.ExternalApi;
import com.jswone.orchestrator.dto.*;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class JomsApi {

  private String jomsBaseUrl;
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

  public InvoicePostedResponse verifyInvoicesPostedStatus(String orderNumber) {
    String baseUrl =
        jomsBaseUrl.concat(externalApi.getServices().get("joms").get("check-invoice-posted"));

    UriComponentsBuilder builder =
        UriComponentsBuilder.fromHttpUrl(baseUrl).queryParam("orderNumber", orderNumber);

    HttpEntity<Void> httpEntity = new HttpEntity<>(this.getHeaders());
    String url = builder.toUriString();
    return this.httpCall(url, HttpMethod.GET, httpEntity, InvoicePostedResponse.class);
  }

  public JomsApiResponse initiateReleaseOrder(
      OrderReleaseTemporalWorkflowRequest orderReleaseTemporalWorkflowRequest) {
    String url = jomsBaseUrl.concat(externalApi.getServices().get("joms").get("release-block"));

    HttpEntity<OrderReleaseTemporalWorkflowRequest> httpEntity =
        new HttpEntity<>(orderReleaseTemporalWorkflowRequest, this.getHeaders());
    ResponseEntity<Map<String, Object>> response =
        restTemplate.exchange(
            url,
            HttpMethod.PUT,
            httpEntity,
            new ParameterizedTypeReference<Map<String, Object>>() {});

    Map<String, Object> body = response.getBody();
    Boolean isSuccess = (Boolean) body.get("isSuccess");
    String message = (String) body.get("message");
    return new JomsApiResponse(isSuccess, message);
  }

  public JomsApiResponse initiateCashback(CashbackPostingRequest cashbackPostingRequest) {
    String url =
        jomsBaseUrl.concat(externalApi.getServices().get("joms").get("post-cashback-note"));

    HttpEntity<CashbackPostingRequest> httpEntity =
        new HttpEntity<>(cashbackPostingRequest, this.getHeaders());
    ResponseEntity<Map<String, Object>> response =
        restTemplate.exchange(
            url,
            HttpMethod.POST,
            httpEntity,
            new ParameterizedTypeReference<Map<String, Object>>() {});

    Map<String, Object> body = response.getBody();
    Boolean isSuccess = (Boolean) body.get("isSuccess");
    String message = (String) body.get("message");
    return new JomsApiResponse(isSuccess, message);
  }

  public JomsApiResponse updateOrderReleaseStatus(OrderReleaseStatusRequest orderReleaseStatusRequest) {
    String url =
        jomsBaseUrl.concat(externalApi.getServices().get("joms").get("update-order-release-status"));

    HttpEntity<OrderReleaseStatusRequest> httpEntity =
        new HttpEntity<>(orderReleaseStatusRequest, this.getHeaders());
    ResponseEntity<Map<String, Object>> response =
            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<Map<String, Object>>() {});

    Map<String, Object> body = response.getBody();
    Boolean isSuccess = (Boolean) body.get("isSuccess");
    String message = (String) body.get("message");
    return new JomsApiResponse(isSuccess, message);
  }
}
