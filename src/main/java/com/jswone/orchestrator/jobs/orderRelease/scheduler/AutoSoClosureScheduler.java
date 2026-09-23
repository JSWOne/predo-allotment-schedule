package com.jswone.orchestrator.jobs.orderRelease.scheduler;

import com.jswone.orchestrator.jobs.orderRelease.workflow.AutoSoClosureWorkflow;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.schedules.Schedule;
import io.temporal.client.schedules.ScheduleActionStartWorkflow;
import io.temporal.client.schedules.ScheduleAlreadyRunningException;
import io.temporal.client.schedules.ScheduleClient;
import io.temporal.client.schedules.ScheduleOptions;
import io.temporal.client.schedules.ScheduleSpec;
import io.temporal.common.RetryOptions;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoSoClosureScheduler {

  private static final String SCHEDULE_ID = "auto-so-closure-daily";

  private final ScheduleClient scheduleClient;

  @Value("${temporal.schedules.auto-so-closure-cron}")
  private String autoSoClosureCron;

  @Value("${temporal.pre-do-allotment-task-queue}")
  private String taskQueue;

  @PostConstruct
  public void registerSchedule() {
    log.info("Registering Temporal schedule '{}' with cron '{}'", SCHEDULE_ID, autoSoClosureCron);
    try {
      scheduleClient.createSchedule(
          SCHEDULE_ID,
          Schedule.newBuilder()
              .setSpec(
                  ScheduleSpec.newBuilder()
                      .setCronExpressions(Collections.singletonList(autoSoClosureCron))
                      .build())
              .setAction(
                  ScheduleActionStartWorkflow.newBuilder()
                      .setWorkflowType(AutoSoClosureWorkflow.class)
                      .setOptions(
                          WorkflowOptions.newBuilder()
                              .setTaskQueue(taskQueue)
                              .setWorkflowId("auto-so-closure-scheduled")
                              .setWorkflowExecutionTimeout(Duration.ofMinutes(30))
                              .setRetryOptions(
                                  RetryOptions.newBuilder().setMaximumAttempts(1).build())
                              .build())
                      .build())
              .build(),
          ScheduleOptions.newBuilder().build());
      log.info("Temporal schedule '{}' registered successfully", SCHEDULE_ID);
    } catch (ScheduleAlreadyRunningException e) {
      log.info("Temporal schedule '{}' already exists, skipping creation", SCHEDULE_ID);
    }
  }
}
