package com.jswone.orchestrator.jobs.orderRelease.activity;

import com.jswone.orchestrator.dto.AttachPreDoResponse;
import com.jswone.orchestrator.dto.EligibleFinishedGoodsResponse;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface PreDoAllotmentChildActivity {

  AttachPreDoResponse processFgUpdateForPreDoAllotment(
      EligibleFinishedGoodsResponse.FinishedGoodsRecord finishedGoodsRecord);
}
