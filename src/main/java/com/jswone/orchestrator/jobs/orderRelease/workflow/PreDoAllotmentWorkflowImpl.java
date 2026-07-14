package com.jswone.orchestrator.jobs.orderRelease.workflow;

import com.jswone.orchestrator.dto.ChildWorkflowResult;
import com.jswone.orchestrator.dto.EligibleFinishedGoodsResponse;
import com.jswone.orchestrator.dto.OrchestratorResponse;
import com.jswone.orchestrator.jobs.orderRelease.activity.PreDoAllotmentActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.List;
import org.slf4j.Logger;

public class PreDoAllotmentWorkflowImpl implements PreDoAllotmentWorkflow {
  private static final Logger log = Workflow.getLogger(PreDoAllotmentWorkflow.class);

  private final PreDoAllotmentActivity activity =
      Workflow.newActivityStub(
          PreDoAllotmentActivity.class,
          ActivityOptions.newBuilder()
              .setStartToCloseTimeout(Duration.ofSeconds(30))
              .setScheduleToStartTimeout(Duration.ofMinutes(2))
              .setScheduleToCloseTimeout(Duration.ofMinutes(3))
              .setRetryOptions(
                  RetryOptions.newBuilder()
                      .setInitialInterval(Duration.ofSeconds(2))
                      .setBackoffCoefficient(2)
                      .setMaximumAttempts(3)
                      .build())
              .build());

  @Override
  public OrchestratorResponse initiatePreDoAllotmentJob() {
    List<EligibleFinishedGoodsResponse.FinishedGoodsRecord> eligibleFgUpdates =
        activity.fetchPendingFGUpdates();

    if (eligibleFgUpdates.isEmpty()) {
      log.info("No FG update records found for pre-do allotment. Skipping.");
      return OrchestratorResponse.builder()
          .isSuccess(false)
          .message("No records found for pre-do allotment")
          .build();
    }

    log.info("Processing {} FG update records sequentially", eligibleFgUpdates.size());

    int successCount = 0;
    int failureCount = 0;

    for (EligibleFinishedGoodsResponse.FinishedGoodsRecord record : eligibleFgUpdates) {
      if (record == null) {
        log.warn("Encountered null record in FG update list, skipping");
        failureCount++;
        continue;
      }
      if (record.getLineItemId() == null) {
        log.warn("Skipping FG record with null lineItemId, fgUpdateId={}", record.getId());
        failureCount++;
        continue;
      }

      String childWorkflowId = Workflow.getInfo().getWorkflowId() + "-" + record.getLineItemId();

      PreDoAllotmentChildWorkflow child =
          Workflow.newChildWorkflowStub(
              PreDoAllotmentChildWorkflow.class,
              ChildWorkflowOptions.newBuilder()
                  .setWorkflowId(childWorkflowId)
                  // no workflow-level retry — activity retries inside child handle transient
                  // failures
                  .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(1).build())
                  // terminate children if parent is cancelled/terminated
                  .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_TERMINATE)
                  .build());

      try {
        // synchronous call — processes one record at a time (sequential)
        ChildWorkflowResult result = child.processFgUpdate(record);
        if (result.isSuccess()) {
          log.info(
              "Successfully processed FG update, fgUpdateId={}, childWorkflowId={}",
              record.getId(),
              childWorkflowId);
          successCount++;
        } else {
          log.warn(
              "FG update processing returned failure, fgUpdateId={}, error={}",
              record.getId(),
              result.getError());
          failureCount++;
        }
      } catch (Exception e) {
        log.error(
            "Child workflow failed after retries, fgUpdateId={}, childWorkflowId={}, cause={}",
            record.getId(),
            childWorkflowId,
            e.getMessage());
        failureCount++;
      }
    }

    log.info("Pre-do allotment job complete. success={}, failure={}", successCount, failureCount);

    boolean allSucceeded = failureCount == 0;
    return OrchestratorResponse.builder()
        .isSuccess(allSucceeded)
        .message(
            String.format(
                "Processed %d records: %d succeeded, %d failed",
                eligibleFgUpdates.size(), successCount, failureCount))
        .build();
  }
}
