package com.jswone.orchestrator.jobs.orderRelease.workflow;

import com.jswone.orchestrator.dto.ChildWorkflowResult;
import com.jswone.orchestrator.dto.EligibleFinishedGoodsResponse;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface PreDoAllotmentChildWorkflow {

  @WorkflowMethod
  ChildWorkflowResult processFgUpdate(
      EligibleFinishedGoodsResponse.FinishedGoodsRecord finishedGoodsRecord);
}
