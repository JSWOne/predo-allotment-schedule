package com.jswone.orchestrator.http.controller;

import com.jswone.orchestrator.dto.OrchestratorResponse;
import com.jswone.orchestrator.dto.enums.NotificationEventType;
import com.jswone.orchestrator.jobs.orderRelease.service.DueNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DueNotificationController {

  private final DueNotificationService dueNotificationService;

  @PostMapping(value = "/initiate-over-due-notification")
  public ResponseEntity<OrchestratorResponse> setUpOrderReleaseJob() {
    log.info("Call received to initiate over due notification scheduler");
    OrchestratorResponse orchestratorResponse =
        dueNotificationService.initiateOverDueNotificationWorkflow(NotificationEventType.OVER_DUE);
    return ResponseEntity.ok(orchestratorResponse);
  }
}
