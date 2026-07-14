package com.jswone.orchestrator.jobs.orderRelease.worker;

import com.jswone.orchestrator.jobs.orderRelease.activity.PreDoAllotmentActivityImpl;
import com.jswone.orchestrator.jobs.orderRelease.activity.PreDoAllotmentChildActivityImpl;
import com.jswone.orchestrator.jobs.orderRelease.workflow.PreDoAllotmentChildWorkflowImpl;
import com.jswone.orchestrator.jobs.orderRelease.workflow.PreDoAllotmentWorkflowImpl;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DueNotificationWorker {

  @Value("${temporal.due-notification-task-queue}")
  private String temporalTaskQueue;

  private final PreDoAllotmentActivityImpl preDoAllotmentActivityImpl;
  private final PreDoAllotmentChildActivityImpl preDoAllotmentChildActivityImpl;
  private final WorkerFactory workerFactory;

  @PostConstruct
  public void registerWorker() {
    Worker worker = workerFactory.newWorker(temporalTaskQueue);
    worker.registerWorkflowImplementationTypes(
        PreDoAllotmentWorkflowImpl.class, PreDoAllotmentChildWorkflowImpl.class);
    worker.registerActivitiesImplementations(
        preDoAllotmentActivityImpl, preDoAllotmentChildActivityImpl);
    log.info("Registered workflows and activities on queue: {}", temporalTaskQueue);
  }

  // Start the factory only once, after the full Spring context is ready.
  // Using @PostConstruct would risk calling start() multiple times if more workers are added.
  @EventListener(ApplicationReadyEvent.class)
  public void startWorkerFactory() {
    try {
      workerFactory.start();
      log.info("Temporal worker factory started on queue: {}", temporalTaskQueue);
    } catch (Exception e) {
      log.error("Failed to start Temporal worker factory", e);
      throw new RuntimeException("Unable to start Temporal worker factory", e);
    }
  }
}
