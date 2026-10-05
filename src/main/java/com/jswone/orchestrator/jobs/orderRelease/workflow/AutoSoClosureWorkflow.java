package com.jswone.orchestrator.jobs.orderRelease.workflow;

import com.jswone.orchestrator.dto.OrchestratorResponse;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface AutoSoClosureWorkflow {

  @WorkflowMethod
  OrchestratorResponse initiateAutoSoClosureJob();
}
