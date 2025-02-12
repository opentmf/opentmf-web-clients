package com.pia.client.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
@ActiveProfiles("junit3")
@Import(ClientAutoConfiguration.class)
class OpenIdWebClient3IT {

  @Autowired
  private WebClient openIdWebClient;

  @Test
  void testContextLoads() {
    Assertions.assertNotNull(openIdWebClient);
  }
}
