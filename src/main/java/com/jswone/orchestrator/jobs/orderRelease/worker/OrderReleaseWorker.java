package com.jswone.orchestrator.jobs.orderRelease.worker;

import com.jswone.orchestrator.jobs.orderRelease.activity.OrderReleaseActivityImpl;
import com.jswone.orchestrator.jobs.orderRelease.workflow.OrderReleaseWorkflowImpl;
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
public class OrderReleaseWorker {

  @Value("${temporal.order-release-task-queue}")
  private final String temporalOrderReleaseTaskQueue;

  private final OrderReleaseActivityImpl orderReleaseActivity;
  private final WorkerFactory workerFactory;

  @PostConstruct
  public void registerWorker() {
    Worker worker = workerFactory.newWorker(temporalOrderReleaseTaskQueue);

    worker.registerWorkflowImplementationTypes(OrderReleaseWorkflowImpl.class);
    worker.registerActivitiesImplementations(orderReleaseActivity);

    try {
      workerFactory.start();
      log.info("Order‑Release worker started on queue: {}", temporalOrderReleaseTaskQueue);
    } catch (Exception e) {
      log.error("Failed to start Order‑Release worker", e);
      throw new RuntimeException("Unable to initiate Order‑Release worker", e);
    }
  }
}
