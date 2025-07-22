package com.jswone.orchestrator.jobs.orderRelease.activity;

import com.jswone.orchestrator.dto.*;
import com.jswone.orchestrator.rest.JomsApi;
import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.failure.ApplicationFailure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderReleaseActivityImpl implements OrderReleaseActivity {

  private final JomsApi jomsApi;

  public OrderReleaseActivityImpl(JomsApi jomsApi) {
    this.jomsApi = jomsApi;
  }

  @Override
  public InvoicePostedResponse checkIfInvoicePosted(String orderNumber) {
    InvoicePostedResponse invoicePostedResponse = jomsApi.verifyInvoicesPostedStatus(orderNumber);
    if (!invoicePostedResponse.getAllInvoicesPosted()) {
      throw ApplicationFailure.newFailure(
          "Invoice Posted for Order failed", "ORDER_RELEASE_INVOICE_POSTED_FAILED", false);
    }
    log.info("Invoice Posted for Order : {}", orderNumber);
    return invoicePostedResponse;
  }

  @Override
  public JomsApiResponse releaseOrderBlock(
      OrderReleaseTemporalWorkflowRequest orderReleaseTemporalWorkflowRequest) {
    ActivityExecutionContext context = Activity.getExecutionContext();
    int attempt = context.getInfo().getAttempt();

    JomsApiResponse response = jomsApi.initiateReleaseOrder(orderReleaseTemporalWorkflowRequest);

    if (!response.getIsSuccess()) {
      if (attempt >= 5) {
        OrderReleaseStatusDto updateReleaseBlockStatus =
            OrderReleaseStatusDto.builder()
                .orderNumber(orderReleaseTemporalWorkflowRequest.getOrderNumber())
                .releaseBlockStatus("CANCELLED")
                .cashbackNoteStatus("CANCELLED")
                .build();

        jomsApi.updateOrderReleaseStatus(updateReleaseBlockStatus);
        log.info(
            "Order release status updated successfully for order: {}",
            updateReleaseBlockStatus.getOrderNumber());
      }

      throw ApplicationFailure.newFailure(
          "Release order block failed", "RELEASE_ORDER_BLOCK_FAILED");
    }

    OrderReleaseStatusDto updateReleaseBlockStatus =
            OrderReleaseStatusDto.builder()
                    .orderNumber(orderReleaseTemporalWorkflowRequest.getOrderNumber())
                    .releaseBlockStatus("SUCCESSFUL")
                    .cashbackNoteStatus("CANCELLED")
                    .build();

    jomsApi.updateOrderReleaseStatus(updateReleaseBlockStatus);

    log.info("Order release for : {}", orderReleaseTemporalWorkflowRequest.getOrderNumber());
    return response;
  }

  public JomsApiResponse publishedCashbackNote(String orderNumber) {
    ActivityExecutionContext context = Activity.getExecutionContext();
    int attempt = context.getInfo().getAttempt();

    CashbackPostingRequest cashbackPostingRequest = new CashbackPostingRequest();
    cashbackPostingRequest.setOrderNumber(orderNumber);
    JomsApiResponse response = jomsApi.initiateCashback(cashbackPostingRequest);

    if (!response.getIsSuccess()) {
      if (attempt >= 5) {
        OrderReleaseStatusDto updateCashbackStatus =
            OrderReleaseStatusDto.builder()
                .orderNumber(orderNumber)
                .releaseBlockStatus("SUCCESSFUL")
                .cashbackNoteStatus("CANCELLED")
                .build();

        jomsApi.updateOrderReleaseStatus(updateCashbackStatus);
        log.info(
            "Cashback status updated successfully for order: {}",
            updateCashbackStatus.getOrderNumber());
      }

      throw ApplicationFailure.newFailure(
          "Publishing cashback note failed", "PUBLISH_CASHBACK_NOTE_FAILED");
    }

    OrderReleaseStatusDto updateCashbackStatus =
            OrderReleaseStatusDto.builder()
                    .orderNumber(orderNumber)
                    .releaseBlockStatus("SUCCESSFUL")
                    .cashbackNoteStatus("SUCCESSFUL")
                    .build();

    jomsApi.updateOrderReleaseStatus(updateCashbackStatus);

    log.info("Cashback release for order : {}", orderNumber);
    return response;
  }
}
