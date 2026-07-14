package com.jswone.orchestrator.jobs.orderRelease.workflow;

import com.jswone.orchestrator.dto.AttachPreDoResponse;
import com.jswone.orchestrator.dto.ChildWorkflowResult;
import com.jswone.orchestrator.dto.EligibleFinishedGoodsResponse;
import com.jswone.orchestrator.jobs.orderRelease.activity.PreDoAllotmentChildActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import org.slf4j.Logger;

public class PreDoAllotmentChildWorkflowImpl implements PreDoAllotmentChildWorkflow {
  private static final Logger log = Workflow.getLogger(PreDoAllotmentChildWorkflowImpl.class);

  private final PreDoAllotmentChildActivity childActivity =
      Workflow.newActivityStub(
          PreDoAllotmentChildActivity.class,
          ActivityOptions.newBuilder()
              .setStartToCloseTimeout(Duration.ofMinutes(2))
              .setScheduleToCloseTimeout(Duration.ofMinutes(5))
              .setRetryOptions(
                  RetryOptions.newBuilder()
                      .setInitialInterval(Duration.ofSeconds(2))
                      .setBackoffCoefficient(2)
                      .setMaximumAttempts(3)
                      .build())
              .build());

  @Override
  public ChildWorkflowResult processFgUpdate(
      EligibleFinishedGoodsResponse.FinishedGoodsRecord finishedGoodsRecord) {

    log.info(
        "Processing FG update in child workflow, fgUpdateId={}, lineItemId={}",
        finishedGoodsRecord.getId(),
        finishedGoodsRecord.getLineItemId());

    // Activity retries (up to 3x with backoff) handle transient failures.
    // If all retries are exhausted the ActivityFailure propagates and Temporal marks this workflow
    // failed,
    // which the parent catches as ChildWorkflowFailure and continues to the next record.
    AttachPreDoResponse response =
        childActivity.processFgUpdateForPreDoAllotment(finishedGoodsRecord);

    if (!response.getIsSuccess()) {
      log.warn(
          "JOMS returned failure for fgUpdateId={}, message={}, requestId={}",
          finishedGoodsRecord.getId(),
          response.getMessage(),
          response.getRequestId());
      return ChildWorkflowResult.builder()
          .fgUpdateId(finishedGoodsRecord.getId())
          .success(false)
          .error(response.getMessage() + " | requestId=" + response.getRequestId())
          .build();
    }

    log.info("FG update successfully allotted, fgUpdateId={}", finishedGoodsRecord.getId());
    return ChildWorkflowResult.builder()
        .fgUpdateId(finishedGoodsRecord.getId())
        .success(true)
        .build();
  }
}
