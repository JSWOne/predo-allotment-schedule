package com.jswone.orchestrator.jobs.orderRelease.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jswone.orchestrator.dto.*;
import com.jswone.orchestrator.http.rest.JomsApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PreDoAllotmentChildActivityImpl implements PreDoAllotmentChildActivity {

  private final JomsApi jomsApi;

  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  public PreDoAllotmentChildActivityImpl(JomsApi jomsApi) {
    this.jomsApi = jomsApi;
  }

  @Override
  public AttachPreDoResponse processFgUpdateForPreDoAllotment(
      EligibleFinishedGoodsResponse.FinishedGoodsRecord finishedGoodsRecord) {
    log.info("Posting FG update id {} to joms for pre-do allotment", finishedGoodsRecord.getId());
    return jomsApi.processFgUpdateForPreDoAllotment(finishedGoodsRecord);
  }
}
