package com.jswone.orchestrator.jobs.orderRelease.activity;

import com.jswone.orchestrator.dto.JomsApiResponse;
import com.jswone.orchestrator.dto.OrderReleaseTemporalWorkflowRequest;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface OrderReleaseActivity {

  @ActivityMethod
  InvoicePostedResponse checkIfInvoicePosted(String orderNumber);

  @ActivityMethod
  JomsApiResponse releaseOrderBlock(
      OrderReleaseTemporalWorkflowRequest orderReleaseTemporalWorkflowRequest);

  @ActivityMethod
  JomsApiResponse publishedCashbackNote(String orderNumber);
}
