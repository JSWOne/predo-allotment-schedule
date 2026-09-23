package com.jswone.orchestrator.jobs.orderRelease.activity;

import com.jswone.orchestrator.dto.StaleOpenSoResponse;
import io.temporal.activity.ActivityInterface;
import java.util.List;
import java.util.Map;

@ActivityInterface
public interface AutoSoClosureActivity {

  StaleOpenSoResponse fetchStaleOpenSos();

  Map<String, Integer> updateStaleOpenSos(List<Long> closedIds);
}
