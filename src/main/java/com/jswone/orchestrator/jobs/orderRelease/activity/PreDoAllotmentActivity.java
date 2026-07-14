package com.jswone.orchestrator.jobs.orderRelease.activity;

import com.jswone.orchestrator.dto.EligibleFinishedGoodsResponse;
import io.temporal.activity.ActivityInterface;
import java.util.List;

@ActivityInterface
public interface PreDoAllotmentActivity {

  List<EligibleFinishedGoodsResponse.FinishedGoodsRecord> fetchPendingFGUpdates();
}
