package com.jswone.orchestrator.controller;

import com.jswone.orchestrator.dto.OrchestratorResponse;
import com.jswone.orchestrator.dto.OrderReleaseTemporalWorkflowRequest;
import com.jswone.orchestrator.jobs.orderRelease.service.OrderReleaseService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(path = "/order-release-job")
public class OrderReleaseController {

  @Autowired private OrderReleaseService orderReleaseService;

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
