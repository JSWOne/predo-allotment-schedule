package com.jswone.orchestrator.rest;

import static org.junit.jupiter.api.Assertions.*;

import com.jswone.orchestrator.config.ExternalApi;
import com.jswone.orchestrator.dto.*;
import com.jswone.orchestrator.http.rest.JomsApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class JomsApiTest {

  @Mock private RestTemplate restTemplate;

  @InjectMocks private JomsApi jomsApi;

  @Mock private ExternalApi externalApi;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(jomsApi, "jomsBaseUrl", "https://qa-oms.msme.jswone.in/joms/api");
  }
}
