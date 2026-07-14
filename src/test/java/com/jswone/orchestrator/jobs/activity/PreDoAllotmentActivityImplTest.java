package com.jswone.orchestrator.jobs.activity;

import com.jswone.orchestrator.dto.OrderReleaseTemporalWorkflowRequest;
import com.jswone.orchestrator.http.rest.JomsApi;
import com.jswone.orchestrator.jobs.orderRelease.activity.PreDoAllotmentActivityImpl;
import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.activity.ActivityInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PreDoAllotmentActivityImplTest {

  @InjectMocks private PreDoAllotmentActivityImpl orderReleaseActivity;

  @Mock private JomsApi jomsApi;

  private final String ORDER_NO = "ORDER123";

  @Mock private ActivityExecutionContext mockContext;
  @Mock private ActivityInfo mockActivityInfo;

  private OrderReleaseTemporalWorkflowRequest request;

  private MockedStatic<Activity> mockedActivity;
  /*
  @Test
  void testCheckIfInvoicePostedWhenAllInvoicesArePosted() {
    InvoicePostedResponse mockResponse = new InvoicePostedResponse();
    mockResponse.setAllInvoicesPosted(true);

    when(jomsApi.verifyInvoicesPostedStatus(ORDER_NO)).thenReturn(mockResponse);

    InvoicePostedResponse result = orderReleaseActivity.checkIfInvoicePosted(ORDER_NO);

    assertNotNull(result);
    assertTrue(result.getAllInvoicesPosted());
  }

  @Test
  void testCheckIfInvoicePostedWhenInvoicesNotPosted() {

    InvoicePostedResponse mockResponse = new InvoicePostedResponse();
    mockResponse.setAllInvoicesPosted(false);

    when(jomsApi.verifyInvoicesPostedStatus(ORDER_NO)).thenReturn(mockResponse);

    ApplicationFailure exception =
        assertThrows(
            ApplicationFailure.class, () -> orderReleaseActivity.checkIfInvoicePosted(ORDER_NO));

    assertEquals("ORDER_RELEASE_INVOICE_POSTED_FAILED", exception.getType());
    assertEquals("Invoice Posted for Order failed", exception.getOriginalMessage());
  }

  @Test
  void testCheckIfInvoicePostedWhenJomsApiReturnsNull() {
    when(jomsApi.verifyInvoicesPostedStatus(ORDER_NO)).thenReturn(null);

    assertThrows(
        NullPointerException.class,
        () -> {
          orderReleaseActivity.checkIfInvoicePosted(ORDER_NO);
        });
  }

  @Test
  void testReleaseOrderSuccess() {
    try (MockedStatic<Activity> mockedActivity = Mockito.mockStatic(Activity.class)) {
      ActivityExecutionContext mockContext = mock(ActivityExecutionContext.class);
      ActivityInfo mockInfo = mock(ActivityInfo.class);

      when(mockInfo.getAttempt()).thenReturn(1);
      when(mockContext.getInfo()).thenReturn(mockInfo);
      mockedActivity.when(Activity::getExecutionContext).thenReturn(mockContext);

      OrderReleaseTemporalWorkflowRequest request = new OrderReleaseTemporalWorkflowRequest();
      request.setOrderNumber("ORD123");

      JomsApiResponse success = new JomsApiResponse(true, "OK");
      when(jomsApi.initiateReleaseOrder(request)).thenReturn(success);

      JomsApiResponse result = orderReleaseActivity.releaseOrderBlock(request);

      assertTrue(result.getIsSuccess());
      assertEquals("OK", result.getMessage());
    }
  }

  @Test
  void testReleaseOrderFailAttemptLessThan5() {
    try (MockedStatic<Activity> mockedActivity = Mockito.mockStatic(Activity.class)) {
      ActivityExecutionContext mockContext = mock(ActivityExecutionContext.class);
      ActivityInfo mockInfo = mock(ActivityInfo.class);

      when(mockInfo.getAttempt()).thenReturn(3);
      when(mockContext.getInfo()).thenReturn(mockInfo);
      mockedActivity.when(Activity::getExecutionContext).thenReturn(mockContext);

      OrderReleaseTemporalWorkflowRequest request = new OrderReleaseTemporalWorkflowRequest();
      request.setOrderNumber("ORD123");

      JomsApiResponse fail = new JomsApiResponse(false, "FAIL");
      when(jomsApi.initiateReleaseOrder(request)).thenReturn(fail);

      ApplicationFailure ex =
          assertThrows(
              ApplicationFailure.class, () -> orderReleaseActivity.releaseOrderBlock(request));

      assertEquals("Release order block failed", ex.getOriginalMessage());
      assertEquals("RELEASE_ORDER_BLOCK_FAILED", ex.getType());

      verify(jomsApi, never()).updateOrderReleaseStatus(any());
    }
  }

  @Test
  void testReleaseOrderFailAttemptMoreThan5() {
    try (MockedStatic<Activity> mockedActivity = Mockito.mockStatic(Activity.class)) {
      ActivityExecutionContext mockContext = mock(ActivityExecutionContext.class);
      ActivityInfo mockInfo = mock(ActivityInfo.class);

      when(mockInfo.getAttempt()).thenReturn(5);
      when(mockContext.getInfo()).thenReturn(mockInfo);
      mockedActivity.when(Activity::getExecutionContext).thenReturn(mockContext);

      OrderReleaseTemporalWorkflowRequest request = new OrderReleaseTemporalWorkflowRequest();
      request.setOrderNumber("ORD123");

      JomsApiResponse fail = new JomsApiResponse(false, "FAIL");
      JomsApiResponse updateResp = new JomsApiResponse(true, "Updated");

      when(jomsApi.initiateReleaseOrder(request)).thenReturn(fail);
      when(jomsApi.updateOrderReleaseStatus(any())).thenReturn(updateResp);

      ApplicationFailure ex =
          assertThrows(
              ApplicationFailure.class, () -> orderReleaseActivity.releaseOrderBlock(request));

      assertEquals("Release order block failed", ex.getOriginalMessage());
      assertEquals("RELEASE_ORDER_BLOCK_FAILED", ex.getType());

      verify(jomsApi)
          .updateOrderReleaseStatus(
              argThat(
                  status ->
                      status.getOrderNumber().equals("ORD123")
                          && status.getReleaseStatus().equals("CANCELLED")
                          && status.getCashbackStatus().equals("PROCESSED")));
    }
  }

  @Test
  void testPublishCashbackNoteSuccess() {
    try (MockedStatic<Activity> mockedActivity = Mockito.mockStatic(Activity.class)) {
      ActivityExecutionContext mockContext = mock(ActivityExecutionContext.class);
      ActivityInfo mockInfo = mock(ActivityInfo.class);

      when(mockInfo.getAttempt()).thenReturn(1);
      when(mockContext.getInfo()).thenReturn(mockInfo);
      mockedActivity.when(Activity::getExecutionContext).thenReturn(mockContext);

      JomsApiResponse success = new JomsApiResponse(true, "Cashback Posted");
      when(jomsApi.initiateCashback(any())).thenReturn(success);

      JomsApiResponse result = orderReleaseActivity.publishedCashbackNote(ORDER_NO);

      assertTrue(result.getIsSuccess());
      assertEquals("Cashback Posted", result.getMessage());
    }
  }

  @Test
  void testPublishCashbackNoteFailAttemptLessThan5() {
    try (MockedStatic<Activity> mockedActivity = Mockito.mockStatic(Activity.class)) {
      ActivityExecutionContext mockContext = mock(ActivityExecutionContext.class);
      ActivityInfo mockInfo = mock(ActivityInfo.class);

      when(mockInfo.getAttempt()).thenReturn(3);
      when(mockContext.getInfo()).thenReturn(mockInfo);
      mockedActivity.when(Activity::getExecutionContext).thenReturn(mockContext);

      JomsApiResponse fail = new JomsApiResponse(false, "FAIL");
      when(jomsApi.initiateCashback(any())).thenReturn(fail);

      ApplicationFailure ex =
          assertThrows(
              ApplicationFailure.class, () -> orderReleaseActivity.publishedCashbackNote(ORDER_NO));

      assertEquals("Publishing cashback note failed", ex.getOriginalMessage());
      assertEquals("PUBLISH_CASHBACK_NOTE_FAILED", ex.getType());

      verify(jomsApi, never()).updateOrderReleaseStatus(any());
    }
  }

  @Test
  void testPublishCashbackNoteFailAttemptMoreThan5() {
    try (MockedStatic<Activity> mockedActivity = Mockito.mockStatic(Activity.class)) {
      ActivityExecutionContext mockContext = mock(ActivityExecutionContext.class);
      ActivityInfo mockInfo = mock(ActivityInfo.class);

      when(mockInfo.getAttempt()).thenReturn(5);
      when(mockContext.getInfo()).thenReturn(mockInfo);
      mockedActivity.when(Activity::getExecutionContext).thenReturn(mockContext);

      JomsApiResponse fail = new JomsApiResponse(false, "FAIL");
      JomsApiResponse updateResponse = new JomsApiResponse(true, "Updated");

      when(jomsApi.initiateCashback(any())).thenReturn(fail);
      when(jomsApi.updateOrderReleaseStatus(any())).thenReturn(updateResponse);

      ApplicationFailure ex =
          assertThrows(
              ApplicationFailure.class, () -> orderReleaseActivity.publishedCashbackNote(ORDER_NO));

      assertEquals("Publishing cashback note failed", ex.getOriginalMessage());
      assertEquals("PUBLISH_CASHBACK_NOTE_FAILED", ex.getType());

      verify(jomsApi)
          .updateOrderReleaseStatus(
              argThat(
                  status ->
                      status.getOrderNumber().equals(ORDER_NO)
                          && status.getReleaseStatus().equals("SUCCESSFUL")
                          && status.getCashbackStatus().equals("CANCELLED")));
    }
  }*/
}
