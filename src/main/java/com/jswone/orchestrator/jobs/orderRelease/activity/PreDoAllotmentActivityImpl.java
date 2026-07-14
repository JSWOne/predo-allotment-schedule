package com.jswone.orchestrator.jobs.orderRelease.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jswone.orchestrator.config.pubsub.PubSubGateway;
import com.jswone.orchestrator.dto.*;
import com.jswone.orchestrator.http.rest.JomsApi;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PreDoAllotmentActivityImpl implements PreDoAllotmentActivity {

  private final JomsApi jomsApi;

  @Autowired private PubSubGateway pubSubGateway;

  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  public PreDoAllotmentActivityImpl(JomsApi jomsApi) {
    this.jomsApi = jomsApi;
  }

  public List<EligibleFinishedGoodsResponse.FinishedGoodsRecord> fetchPendingFGUpdates() {
    log.info("Fetching list of fg updates eligible for pre-do allotment");
    EligibleFinishedGoodsResponse response = jomsApi.fetchEligibleFgUpdatesForPreDoAllotment();

    if (response == null || response.getRecords() == null) {
      log.info("No records fetched from joms for pre-do allotment");
      return List.of();
    }
    log.info("Fetched {} eligible FG update records from joms", response.getRecords().size());
    return response.getRecords();
  }
}
