package com.jswone.orchestrator.jobs.orderRelease.worker;

import com.jswone.orchestrator.jobs.orderRelease.activity.DueNotificationActivityImpl;
import com.jswone.orchestrator.jobs.orderRelease.activity.DueNotificationChildActivityImpl;
import com.jswone.orchestrator.jobs.orderRelease.workflow.DueNotificationWorkflowImpl;
import com.jswone.orchestrator.jobs.orderRelease.workflow.GstinNotificationChildWorkflowImpl;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DueNotificationWorker {

  @Value("${temporal.due-notification-task-queue}")
  private String temporalOrderReleaseTaskQueue;

  private final DueNotificationActivityImpl dueNotificationActivity;
  private final DueNotificationChildActivityImpl dueNotificationChildActivityImpl;
  private final WorkerFactory workerFactory;

  @PostConstruct
  public void registerWorker() {
    Worker worker = workerFactory.newWorker(temporalOrderReleaseTaskQueue);

    worker.registerWorkflowImplementationTypes(
        DueNotificationWorkflowImpl.class, GstinNotificationChildWorkflowImpl.class);
    worker.registerActivitiesImplementations(
        dueNotificationActivity, dueNotificationChildActivityImpl);

    try {
      workerFactory.start();
      log.info("Due‑notification worker started on queue: {}", temporalOrderReleaseTaskQueue);
    } catch (Exception e) {
      log.error("Failed to start Order‑Release worker", e);
      throw new RuntimeException("Unable to initiate Order‑Release worker", e);
    }
  }
}
