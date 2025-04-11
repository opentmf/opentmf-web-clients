package org.opentmf.client.test;

import static org.opentmf.client.test.util.MockServerUtils.BASE_URL;
import static org.opentmf.commons.util.JacksonUtil.contents;

import org.opentmf.client.common.exception.OpenTmfWebClientException;
import org.opentmf.client.openid.model.OpenidClientProperties;
import org.opentmf.client.openid.model.OpenidClients;
import org.opentmf.client.openid.service.api.OpenidTokenService;
import org.opentmf.client.openid.service.api.OpenidWebClientProvider;
import org.opentmf.client.test.util.MockServerUtils;
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

  @Autowired private OpenidClients openidClients;
  @Autowired private OpenidWebClientProvider openidWebClientProvider;

  @Autowired private OpenidClientProperties firstOpenIdClientProperties;
  @Autowired private WebClient firstOpenIdWebClient;
  @Autowired private OpenidTokenService firstOpenIdTokenService;

  @Autowired private OpenidClientProperties secondOpenIdClientProperties;
  @Autowired private WebClient secondOpenIdWebClient;
  @Autowired private OpenidTokenService secondOpenIdTokenService;


  @AfterEach
  void afterEach() {
    MockServerUtils.resetMockServer();
  }

  @Test
  void testOpenidAuth_withCorrectConfiguration_exposesBeans() {
    Assertions.assertNotNull(openidClients);

    Assertions.assertNotNull(firstOpenIdClientProperties);
    Assertions.assertNotNull(firstOpenIdWebClient);
    Assertions.assertNotNull(firstOpenIdTokenService);

    Assertions.assertNotNull(secondOpenIdClientProperties);
    Assertions.assertNotNull(secondOpenIdWebClient);
    Assertions.assertNotNull(secondOpenIdTokenService);

    Assertions.assertNotEquals(firstOpenIdClientProperties, secondOpenIdClientProperties);
    Assertions.assertNotEquals(firstOpenIdWebClient, secondOpenIdWebClient);
    Assertions.assertNotEquals(firstOpenIdTokenService, secondOpenIdTokenService);

    Assertions.assertEquals(firstOpenIdWebClient, openidWebClientProvider.buildWebClient(firstOpenIdClientProperties));
    Assertions.assertEquals(firstOpenIdTokenService, openidWebClientProvider.buildTokenService(firstOpenIdClientProperties));

    Assertions.assertEquals(secondOpenIdWebClient, openidWebClientProvider.buildWebClient(secondOpenIdClientProperties));
    Assertions.assertEquals(secondOpenIdTokenService, openidWebClientProvider.buildTokenService(secondOpenIdClientProperties));
  }

  @Test
  void testOpenidAuthTokenCache_withCorrectConfiguration_expiresWithinConfiguredDuretion() {
    firstOpenIdClientProperties.getTokenConfig().setTokenUrl(URI.create(BASE_URL + "/token"));
    MockServerUtils.post("/token", 1, contents("json/openid-token.json"), HttpStatus.OK);

    StepVerifier.create(firstOpenIdTokenService.getToken())
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
      StepVerifier.create(firstOpenIdTokenService.getToken())
          .expectErrorMatches(throwable -> {
            Assertions.assertInstanceOf(OpenTmfWebClientException.class, throwable);
            OpenTmfWebClientException e = (OpenTmfWebClientException) throwable;
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
