package com.jswone.orchestrator.jobs.orderRelease.activity;

import com.jswone.orchestrator.dto.StaleOpenSoResponse;
import com.jswone.orchestrator.http.rest.JomsApi;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AutoSoClosureActivityImpl implements AutoSoClosureActivity {

  private final JomsApi jomsApi;

  public AutoSoClosureActivityImpl(JomsApi jomsApi) {
    this.jomsApi = jomsApi;
  }

  @Override
  public StaleOpenSoResponse fetchStaleOpenSos() {
    log.info("Calling JOMS to fetch stale open SOs");
    StaleOpenSoResponse response = jomsApi.fetchStaleOpenSos();

    if (response == null || response.getData() == null) {
      log.info("No stale open SO records returned from JOMS");
      return StaleOpenSoResponse.builder().success(false).build();
    }

    log.info(
        "Fetched stale open SOs from JOMS: total={}, closed={}, abandoned={}",
        response.getTotalCount(),
        response.getClosedCount(),
        response.getAbandonedCount());
    return response;
  }

  @Override
  public Map<String, Integer> updateStaleOpenSos(List<Long> closedIds) {
    log.info("Calling JOMS to update stale open SOs with {} closed ids", closedIds.size());
    Map<String, Integer> result = jomsApi.updateStaleOpenSos(closedIds);
    log.info("JOMS update stale open SO result: {}", result);
    return result;
  }
}
