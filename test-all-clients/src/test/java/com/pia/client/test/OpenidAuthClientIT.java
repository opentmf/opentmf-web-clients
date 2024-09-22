package com.pia.client.test;

import static com.pia.client.test.util.MockServerUtils.BASE_URL;
import static com.pia.commons.util.JacksonUtil.contents;

import com.pia.client.common.exception.PiaWebClientException;
import com.pia.client.openid.model.OpenidClientProperties;
import com.pia.client.openid.model.OpenidClients;
import com.pia.client.openid.service.api.OpenidTokenService;
import com.pia.client.test.util.MockServerUtils;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

/**
 * @author Gokhan Demir
 */
@SpringBootTest
@Slf4j
class OpenidAuthClientIT {

  @Autowired
  private WebClient openidWebClient;

  @Autowired
  private OpenidTokenService openidTokenService;

  @Autowired
  private OpenidClients openidClients;

  @AfterEach
  void afterEach() {
    MockServerUtils.resetMockServer();
  }

  @Test
  void testOpenidAuth_withCorrectConfiguration_exposesBeans() {
    Assertions.assertNotNull(openidClients);
    Assertions.assertNotNull(openidWebClient);
    Assertions.assertNotNull(openidTokenService);
  }

  @Test
  void testOpenidAuthTokenCache_withCorrectConfiguration_expiresWithinConfiguredDuretion() {
    OpenidClientProperties client1 = openidClients.getOpenid().get("client1");
    Assertions.assertNotNull(client1);
    client1.getTokenConfig().setTokenUrl(URI.create(BASE_URL + "/token"));
    MockServerUtils.post("/token", 1, contents("json/openid-token.json"), HttpStatus.OK);

    StepVerifier.create(openidTokenService.getToken())
        .assertNext(StringUtils::hasText)
        .verifyComplete();

    MockServerUtils.post("/token", 100, "", HttpStatus.NOT_FOUND);

    Awaitility.await()
        .pollDelay(500, TimeUnit.MILLISECONDS)
        .pollInterval(100, TimeUnit.MILLISECONDS)
        .atMost(2, TimeUnit.SECONDS)
        .until(this::notFound);
  }

  private boolean notFound() {
    log.debug("In notFound() method.");
    try {
      StepVerifier.create(openidTokenService.getToken())
          .expectErrorMatches(throwable -> {
            Assertions.assertInstanceOf(PiaWebClientException.class, throwable);
            PiaWebClientException e = (PiaWebClientException) throwable;
            return e.getRawStatusCode() == 404;
          })
          .verify(Duration.ofMillis(100L));
    } catch (AssertionError e) {
      log.debug("Returning false");
      return false;
    }
    log.debug("Returning TRUE");
    return true;
  }
}
