package com.jswone.orchestrator.jobs.orderRelease.scheduler;

import com.jswone.orchestrator.jobs.orderRelease.service.PreDoAllotmentServiceImpl;
import com.jswone.orchestrator.jobs.orderRelease.workflow.PreDoAllotmentWorkflow;
import io.temporal.client.schedules.Schedule;
import io.temporal.client.schedules.ScheduleActionStartWorkflow;
import io.temporal.client.schedules.ScheduleAlreadyRunningException;
import io.temporal.client.schedules.ScheduleClient;
import io.temporal.client.schedules.ScheduleOptions;
import io.temporal.client.schedules.ScheduleSpec;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreDoAllotmentScheduler {

  private static final String SCHEDULE_ID = "fg-pre-do-allotment-daily";

  private final ScheduleClient scheduleClient;
  private final PreDoAllotmentServiceImpl preDoAllotmentService;

  @Value("${temporal.schedules.pre-do-allotment-cron}")
  private String preDoAllotmentCron;

  @PostConstruct
  public void registerSchedule() {
    log.info("Registering Temporal schedule '{}' with cron '{}'", SCHEDULE_ID, preDoAllotmentCron);
    try {
      scheduleClient.createSchedule(
          SCHEDULE_ID,
          Schedule.newBuilder()
              .setSpec(
                  ScheduleSpec.newBuilder()
                      .setCronExpressions(Collections.singletonList(preDoAllotmentCron))
                      .build())
              .setAction(
                  ScheduleActionStartWorkflow.newBuilder()
                      .setWorkflowType(PreDoAllotmentWorkflow.class)
                      .setOptions(
                          preDoAllotmentService.buildWorkflowOptions(
                              "fg-pre-do-allotment-scheduled"))
                      .build())
              .build(),
          ScheduleOptions.newBuilder().build());
      log.info("Temporal schedule '{}' registered successfully", SCHEDULE_ID);
    } catch (ScheduleAlreadyRunningException e) {
      log.info("Temporal schedule '{}' already exists, skipping creation", SCHEDULE_ID);
    }
  }
}
