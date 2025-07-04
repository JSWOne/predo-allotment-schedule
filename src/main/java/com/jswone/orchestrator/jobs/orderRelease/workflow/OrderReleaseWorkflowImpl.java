package com.jswone.orchestrator.jobs.orderRelease.workflow;

import com.jswone.orchestrator.dto.JomsApiResponse;
import com.jswone.orchestrator.dto.OrderReleaseTemporalWorkflowRequest;
import com.jswone.orchestrator.jobs.orderRelease.activity.OrderReleaseActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderReleaseWorkflowImpl implements OrderReleaseWorkflow {

  OrderReleaseActivity loopActivityStub =
      Workflow.newActivityStub(
          OrderReleaseActivity.class,
          ActivityOptions.newBuilder()
              .setRetryOptions(
                  RetryOptions.newBuilder()
                      .setInitialInterval(Duration.ofMinutes(10))
                      .setBackoffCoefficient(1.0)
                      .build())
              .setStartToCloseTimeout(Duration.ofSeconds(30))
              .build());

  OrderReleaseActivity retryLimitedStub =
      Workflow.newActivityStub(
          OrderReleaseActivity.class,
          ActivityOptions.newBuilder()
              .setRetryOptions(
                  RetryOptions.newBuilder()
                      .setInitialInterval(Duration.ofSeconds(2))
                      .setBackoffCoefficient(2)
                      .setMaximumAttempts(5)
                      .setMaximumInterval(Duration.ofSeconds(60))
                      .build())
              .setStartToCloseTimeout(Duration.ofSeconds(60))
              .build());

  @Override
  public void initiateOrderReleaseJob(
      OrderReleaseTemporalWorkflowRequest orderReleaseTemporalWorkflowRequest) {
    String orderNumber = orderReleaseTemporalWorkflowRequest.getOrderNumber();
    log.info("Workflow started for {}", orderNumber);

    InvoicePostedResponse response = loopActivityStub.checkIfInvoicePosted(orderNumber);
    log.info(
        "Invoice status: {} for {}, Message: {}",
        response.getIsSuccess(),
        orderNumber,
        response.getMessage());

    orderReleaseTemporalWorkflowRequest.setIsOmsCall(false);
    JomsApiResponse jomsResponse =
        retryLimitedStub.releaseOrderBlock(orderReleaseTemporalWorkflowRequest);
    log.info(
        "Order release for {}, status: {}, Message: {}",
        orderNumber,
        jomsResponse.getIsSuccess(),
        jomsResponse.getMessage());

    jomsResponse = retryLimitedStub.publishedCashbackNote(orderNumber);
    log.info(
        "Cashback note published for {}, status : {}, Message : {}",
        orderNumber,
        jomsResponse.getIsSuccess(),
        jomsResponse.getMessage());
  }
}
