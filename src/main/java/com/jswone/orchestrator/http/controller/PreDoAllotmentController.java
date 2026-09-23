package com.jswone.orchestrator.http.controller;

import com.jswone.orchestrator.dto.OrchestratorResponse;
import com.jswone.orchestrator.jobs.orderRelease.service.AutoSoClosureService;
import com.jswone.orchestrator.jobs.orderRelease.service.PreDoAllotmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PreDoAllotmentController {

  private final PreDoAllotmentService preDoAllotmentService;
  private final AutoSoClosureService autoSoClosureService;

  @PostMapping(value = "/initiate-fg-pre-do-allotment-scheduler")
  public ResponseEntity<OrchestratorResponse> initiateGgPreDoAllotmentScheduler() {
    log.info("Call received to initiate pre do allotment notification scheduler");
    OrchestratorResponse orchestratorResponse =
        preDoAllotmentService.initiateFgPreDoAllotmentScheduler();
    return ResponseEntity.ok(orchestratorResponse);
  }

  @PostMapping(value = "/healthcheck")
  public ResponseEntity<String> healthcheck() {
    log.info("Request for health check");
    return ResponseEntity.ok("health check success");
  }

  @PostMapping(value = "/initiate-auto-so-closure-scheduler")
  public ResponseEntity<OrchestratorResponse> initiateAutoSoClosureScheduler() {
    log.info("Call received to initiate auto SO closure scheduler");
    OrchestratorResponse orchestratorResponse =
        autoSoClosureService.initiateAutoSoClosureScheduler();
    return ResponseEntity.ok(orchestratorResponse);
  }
}
