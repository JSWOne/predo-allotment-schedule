package com.jswone.orchestrator.rest;

import static org.junit.jupiter.api.Assertions.*;

import com.jswone.orchestrator.config.ExternalApi;
import com.jswone.orchestrator.dto.*;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class JomsApiTest {

  @Mock private RestTemplate restTemplate;

  @InjectMocks private JomsApi jomsApi;

  @Mock private ExternalApi externalApi;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(jomsApi, "jomsBaseUrl", "https://qa-oms.msme.jswone.in/joms/api");
  }

  @Test
  void testVerifyInvoicesPostedStatusSuccessResponse() {
    String orderNumber = "ORD123";
    String path = "/external/v1/customer-order/check-invoice-posted";

    Map<String, String> jomsServices = new HashMap<>();
    jomsServices.put("check-invoice-posted", path);

    Map<String, Map<String, String>> services = new HashMap<>();
    services.put("joms", jomsServices);

    Mockito.when(externalApi.getServices()).thenReturn(services);

    InvoicePostedResponse mockBody =
        InvoicePostedResponse.builder()
            .isSuccess(true)
            .message("Success")
            .allInvoicesPosted(true)
            .build();

    ResponseEntity<InvoicePostedResponse> mockResponse =
        new ResponseEntity<>(mockBody, HttpStatus.OK);

    Mockito.when(
            restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.eq(InvoicePostedResponse.class)))
        .thenReturn(mockResponse);

    InvoicePostedResponse result = jomsApi.verifyInvoicesPostedStatus(orderNumber);

    assertNotNull(result);
    assertTrue(result.getIsSuccess());
    assertEquals("Success", result.getMessage());
  }

  @Test
  void testVerifyInvoicesPostedStatusFailureResponse() {
    String orderNumber = "ORD123";
    String path = "/external/v1/customer-order/check-invoice-posted";

    Map<String, String> jomsServices = new HashMap<>();
    jomsServices.put("check-invoice-posted", path);

    Map<String, Map<String, String>> services = new HashMap<>();
    services.put("joms", jomsServices);

    Mockito.when(externalApi.getServices()).thenReturn(services);

    ResponseEntity<InvoicePostedResponse> responseEntity =
        new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

    Mockito.when(
            restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.eq(InvoicePostedResponse.class)))
        .thenReturn(responseEntity);

    InvoicePostedResponse result = jomsApi.verifyInvoicesPostedStatus(orderNumber);

    assertNull(result);
  }

  @Test
  void testVerifyInvoicesPostedStatusNullRequest() {
    String orderNumber = null;
    String path = "/external/v1/customer-order/check-invoice-posted";

    Map<String, String> jomsServices = new HashMap<>();
    jomsServices.put("check-invoice-posted", path);

    Map<String, Map<String, String>> services = new HashMap<>();
    services.put("joms", jomsServices);

    Mockito.when(externalApi.getServices()).thenReturn(services);

    assertThrows(NullPointerException.class, () -> jomsApi.verifyInvoicesPostedStatus(orderNumber));
  }

  @Test
  void testInitiateReleaseOrderSuccessResponse() {
    OrderReleaseTemporalWorkflowRequest request = new OrderReleaseTemporalWorkflowRequest();

    Map<String, String> jomsServices = new HashMap<>();
    jomsServices.put("release-block", "/internal/v1/customer-order/release-block");

    Map<String, Map<String, String>> services = new HashMap<>();
    services.put("joms", jomsServices);

    Mockito.when(externalApi.getServices()).thenReturn(services);

    Map<String, Object> mockMap = new HashMap<>();
    mockMap.put("isSuccess", true);
    mockMap.put("message", "Success");

    ResponseEntity<Map<String, Object>> responseEntity =
        new ResponseEntity<>(mockMap, HttpStatus.OK);

    Mockito.when(
            restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.PUT),
                ArgumentMatchers.<HttpEntity<OrderReleaseTemporalWorkflowRequest>>any(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()))
        .thenReturn(responseEntity);

    JomsApiResponse response = jomsApi.initiateReleaseOrder(request);

    assertTrue(response.getIsSuccess());
    assertEquals("Success", response.getMessage());
  }

  @Test
  void testInitiateReleaseOrderFailureResponse() {
    OrderReleaseTemporalWorkflowRequest request = new OrderReleaseTemporalWorkflowRequest();

    Map<String, String> jomsServices = new HashMap<>();
    jomsServices.put("release-block", "/internal/v1/customer-order/release-block");

    Map<String, Map<String, String>> services = new HashMap<>();
    services.put("joms", jomsServices);

    Mockito.when(externalApi.getServices()).thenReturn(services);

    Map<String, Object> mockMap = new HashMap<>();
    mockMap.put("isSuccess", false);
    mockMap.put("message", "Order Release Failed");

    ResponseEntity<Map<String, Object>> responseEntity =
        new ResponseEntity<>(mockMap, HttpStatus.OK);

    Mockito.when(
            restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.PUT),
                ArgumentMatchers.<HttpEntity<OrderReleaseTemporalWorkflowRequest>>any(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()))
        .thenReturn(responseEntity);

    JomsApiResponse response = jomsApi.initiateReleaseOrder(request);

    assertFalse(response.getIsSuccess());
    assertEquals("Order Release Failed", response.getMessage());
  }

  @Test
  void testInitiateReleaseOrderException() {
    OrderReleaseTemporalWorkflowRequest request = new OrderReleaseTemporalWorkflowRequest();

    Map<String, String> jomsServices = new HashMap<>();
    jomsServices.put("release-block", "/internal/v1/customer-order/release-block");

    Map<String, Map<String, String>> services = new HashMap<>();
    services.put("joms", jomsServices);

    Mockito.when(externalApi.getServices()).thenReturn(services);

    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

    Mockito.when(
            restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.PUT),
                ArgumentMatchers.<HttpEntity<OrderReleaseTemporalWorkflowRequest>>any(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()))
        .thenReturn(responseEntity);

    assertThrows(NullPointerException.class, () -> jomsApi.initiateReleaseOrder(request));
  }

  @Test
  void testInitiateReleaseOrderNullRequest() {
    assertThrows(NullPointerException.class, () -> jomsApi.initiateReleaseOrder(null));
  }

  @Test
  void testInitiateCashbackSuccessResponse() {
    CashbackPostingRequest cashbackPostingRequest = new CashbackPostingRequest();
    Map<String, String> jomsServices = new HashMap<>();
    jomsServices.put("post-cashback-note", "/external/v1/customer-order/post-cashback-note");

    Map<String, Map<String, String>> services = new HashMap<>();
    services.put("joms", jomsServices);

    Mockito.when(externalApi.getServices()).thenReturn(services);

    Map<String, Object> mockMap = new HashMap<>();
    mockMap.put("isSuccess", true);
    mockMap.put("message", "Success");

    ResponseEntity<Map<String, Object>> responseEntity =
        new ResponseEntity<>(mockMap, HttpStatus.OK);

    Mockito.when(
            restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.POST),
                ArgumentMatchers.<HttpEntity<CashbackPostingRequest>>any(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()))
        .thenReturn(responseEntity);

    JomsApiResponse response = jomsApi.initiateCashback(cashbackPostingRequest);

    assertTrue(response.getIsSuccess());
    assertEquals("Success", response.getMessage());
  }

  @Test
  void testInitiateCashbackFailureResponse() {
    CashbackPostingRequest cashbackPostingRequest = new CashbackPostingRequest();
    Map<String, String> jomsServices = new HashMap<>();
    jomsServices.put("post-cashback-note", "/external/v1/customer-order/post-cashback-note");

    Map<String, Map<String, String>> services = new HashMap<>();
    services.put("joms", jomsServices);

    Mockito.when(externalApi.getServices()).thenReturn(services);

    Map<String, Object> mockMap = new HashMap<>();
    mockMap.put("isSuccess", false);
    mockMap.put("message", "Cashback Failed");

    ResponseEntity<Map<String, Object>> responseEntity =
        new ResponseEntity<>(mockMap, HttpStatus.OK);

    Mockito.when(
            restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.POST),
                ArgumentMatchers.<HttpEntity<CashbackPostingRequest>>any(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()))
        .thenReturn(responseEntity);

    JomsApiResponse response = jomsApi.initiateCashback(cashbackPostingRequest);

    assertFalse(response.getIsSuccess());
    assertEquals("Cashback Failed", response.getMessage());
  }

  @Test
  void testInitiateCashbackException() {
    CashbackPostingRequest cashbackPostingRequest = new CashbackPostingRequest();
    Map<String, String> jomsServices = new HashMap<>();
    jomsServices.put("post-cashback-note", "/external/v1/customer-order/post-cashback-note");

    Map<String, Map<String, String>> services = new HashMap<>();
    services.put("joms", jomsServices);

    Mockito.when(externalApi.getServices()).thenReturn(services);
    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

    Mockito.when(
            restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.POST),
                ArgumentMatchers.<HttpEntity<CashbackPostingRequest>>any(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()))
        .thenReturn(responseEntity);

    assertThrows(
        NullPointerException.class, () -> jomsApi.initiateCashback(cashbackPostingRequest));
  }

  @Test
  void testInitiateCashbackNullRequest() {
    assertThrows(NullPointerException.class, () -> jomsApi.initiateCashback(null));
  }

  @Test
  void testUpdateOrderReleaseStatusSuccessResponse() {
    OrderReleaseStatusRequest orderReleaseStatusRequest = new OrderReleaseStatusRequest();

    Map<String, String> jomsServices = new HashMap<>();
    jomsServices.put(
        "update-order-release-status", "/external/v1/customer-order/update-order-release-status");

    Map<String, Map<String, String>> services = new HashMap<>();
    services.put("joms", jomsServices);

    Mockito.when(externalApi.getServices()).thenReturn(services);

    Map<String, Object> mockMap = new HashMap<>();
    mockMap.put("isSuccess", true);
    mockMap.put("message", "Success");

    ResponseEntity<Map<String, Object>> responseEntity =
        new ResponseEntity<>(mockMap, HttpStatus.OK);

    Mockito.when(
            restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.POST),
                ArgumentMatchers.<HttpEntity<OrderReleaseStatusRequest>>any(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()))
        .thenReturn(responseEntity);

    JomsApiResponse response = jomsApi.updateOrderReleaseStatus(orderReleaseStatusRequest);

    assertTrue(response.getIsSuccess());
    assertEquals("Success", response.getMessage());
  }

  @Test
  void testUpdateOrderReleaseStatusFailureResponse() {
    OrderReleaseStatusRequest orderReleaseStatusRequest = new OrderReleaseStatusRequest();
    Map<String, String> jomsServices = new HashMap<>();
    jomsServices.put(
        "update-order-release-status", "/external/v1/customer-order/update-order-release-status");

    Map<String, Map<String, String>> services = new HashMap<>();
    services.put("joms", jomsServices);

    Mockito.when(externalApi.getServices()).thenReturn(services);

    Map<String, Object> mockMap = new HashMap<>();
    mockMap.put("isSuccess", false);
    mockMap.put("message", "Update failed");

    ResponseEntity<Map<String, Object>> responseEntity =
        new ResponseEntity<>(mockMap, HttpStatus.OK);

    Mockito.when(
            restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.POST),
                ArgumentMatchers.<HttpEntity<OrderReleaseStatusRequest>>any(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()))
        .thenReturn(responseEntity);

    JomsApiResponse response = jomsApi.updateOrderReleaseStatus(orderReleaseStatusRequest);

    assertFalse(response.getIsSuccess());
    assertEquals("Update failed", response.getMessage());
  }

  @Test
  void testUpdateOrderReleaseStatusException() {
    OrderReleaseStatusRequest orderReleaseStatusRequest = new OrderReleaseStatusRequest();
    Map<String, String> jomsServices = new HashMap<>();
    jomsServices.put(
        "update-order-release-status", "/external/v1/customer-order/update-order-release-status");

    Map<String, Map<String, String>> services = new HashMap<>();
    services.put("joms", jomsServices);

    Mockito.when(externalApi.getServices()).thenReturn(services);

    ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

    Mockito.when(
            restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.POST),
                ArgumentMatchers.<HttpEntity<OrderReleaseStatusRequest>>any(),
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()))
        .thenReturn(responseEntity);

    assertThrows(
        NullPointerException.class,
        () -> jomsApi.updateOrderReleaseStatus(orderReleaseStatusRequest));
  }

  @Test
  void testUpdateOrderReleaseStatusNullRequest() {
    assertThrows(NullPointerException.class, () -> jomsApi.updateOrderReleaseStatus(null));
  }
}
