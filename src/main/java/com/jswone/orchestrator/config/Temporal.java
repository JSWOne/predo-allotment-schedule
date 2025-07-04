package com.jswone.orchestrator.config;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Temporal {

  @Value("${temporal.server-base-url}")
  private String temporalBaseUrl;

  @Value("${temporal.namespace}")
  private String temporalNameSpace;

  @Value("${temporal.invoice-rejection-task-queue}")
  private String temporalInvoiceRejectionTaskQueue;

  @Bean
  public WorkflowServiceStubs workflowServiceStubs() {
    return WorkflowServiceStubs.newInstance(
        WorkflowServiceStubsOptions.newBuilder().setTarget(temporalBaseUrl).build());
  }

  @Bean
  public WorkflowClient workflowClient(WorkflowServiceStubs workflowServiceStubs) {
    return WorkflowClient.newInstance(
        workflowServiceStubs,
        WorkflowClientOptions.newBuilder().setNamespace(temporalNameSpace).build());
  }

  @Bean
  public WorkerFactory workerFactory(WorkflowClient workflowClient) {
    return WorkerFactory.newInstance(workflowClient);
  }
}
