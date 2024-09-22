package com.pia.client.test;

import com.pia.client.basic.service.api.BasicTokenService;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

/**
 * @author Gokhan Demir
 */
@SpringBootTest
class BasicAuthClientIT {

  @Autowired
  private WebClient basicWebClient;

  @Autowired
  private BasicTokenService basicTokenService;

  @Test
  void testBasicAuth_withCorrectConfiguration_exposesBeans() {
    Assertions.assertNotNull(basicWebClient);
    Assertions.assertNotNull(basicTokenService);
    StepVerifier.create(basicTokenService.getToken())
        .expectNextMatches(Objects::nonNull)
        .verifyComplete();
  }

}
