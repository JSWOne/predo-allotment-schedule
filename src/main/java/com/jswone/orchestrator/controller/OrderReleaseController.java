package com.jswone.orchestrator.controller;

import com.jswone.orchestrator.dto.OrchestratorResponse;
import com.jswone.orchestrator.dto.OrderReleaseTemporalWorkflowRequest;
import com.jswone.orchestrator.jobs.orderRelease.service.OrderReleaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderReleaseController {

  private final OrderReleaseService orderReleaseService;

  @PostMapping(value = "/initiate")
  public ResponseEntity<OrchestratorResponse> setUpOrderReleaseJob(
      @RequestBody OrderReleaseTemporalWorkflowRequest orderReleaseTemporalWorkflowRequest) {
    orderReleaseService.initiateOrderReleaseWorkflow(orderReleaseTemporalWorkflowRequest);
    OrchestratorResponse commonWorkflowResponse =
        OrchestratorResponse.builder()
            .isSuccess(Boolean.TRUE)
            .message("workflow triggered for order release")
            .build();
    return ResponseEntity.ok(commonWorkflowResponse);
  }
}
