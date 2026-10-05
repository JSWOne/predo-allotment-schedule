package com.jswone.orchestrator.jobs.orderRelease.workflow;

import com.jswone.orchestrator.dto.OrchestratorResponse;
import com.jswone.orchestrator.dto.StaleOpenSoResponse;
import com.jswone.orchestrator.jobs.orderRelease.activity.AutoSoClosureActivity;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;

public class AutoSoClosureWorkflowImpl implements AutoSoClosureWorkflow {

  private static final Logger log = Workflow.getLogger(AutoSoClosureWorkflow.class);

  private final AutoSoClosureActivity activity =
      Workflow.newActivityStub(
          AutoSoClosureActivity.class,
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
  public OrchestratorResponse initiateAutoSoClosureJob() {
    StaleOpenSoResponse fetchResponse = activity.fetchStaleOpenSos();

    if (fetchResponse == null
        || fetchResponse.getData() == null
        || fetchResponse.getData().isEmpty()) {
      log.info("No stale open SO records found. Skipping.");
      return OrchestratorResponse.builder()
          .isSuccess(false)
          .message("No stale open SO records found")
          .build();
    }

    log.info(
        "Fetched stale open SOs: total={}, closedIds={}, abandonedIds={}",
        fetchResponse.getTotalCount(),
        fetchResponse.getClosedIds(),
        fetchResponse.getAbandonedIds());

    if (fetchResponse.getClosedIds() == null || fetchResponse.getClosedIds().isEmpty()) {
      log.info("No closed IDs to update. Skipping update step.");
      return OrchestratorResponse.builder()
          .isSuccess(true)
          .message("No closed SO IDs to update")
          .build();
    }

    Map<String, Integer> updateResult = activity.updateStaleOpenSos(fetchResponse.getClosedIds());

    log.info("Auto SO closure job complete. updateResult={}", updateResult);

    return OrchestratorResponse.builder()
        .isSuccess(true)
        .message(
            String.format(
                "Processed %d stale open SOs: updated closed IDs=%s, result=%s",
                fetchResponse.getTotalCount(), fetchResponse.getClosedIds(), updateResult))
        .build();
  }
}
