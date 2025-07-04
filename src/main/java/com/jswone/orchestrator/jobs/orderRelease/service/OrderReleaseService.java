package com.jswone.orchestrator.jobs.orderRelease.service;

import com.jswone.orchestrator.dto.OrderReleaseTemporalWorkflowRequest;

public interface OrderReleaseService {
  void initiateOrderReleaseWorkflow(
      OrderReleaseTemporalWorkflowRequest orderReleaseTemporalWorkflowRequest);
}
