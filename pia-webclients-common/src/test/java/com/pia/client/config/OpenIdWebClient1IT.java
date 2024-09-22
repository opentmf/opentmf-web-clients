package com.pia.client.config;

import static com.pia.client.common.util.TokenUtil.TOKEN_TYPE_BEARER;

import com.pia.client.common.service.api.TokenService;
import com.pia.client.util.MockServerUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("junit")
@Import(OpenIdClientAutoConfiguration.class)
class OpenIdWebClient1IT {

  private static final String ENDPOINT = "/endPoint";
  private static final String URL = MockServerUtils.BASE_URL + ENDPOINT;

  @Autowired
  private WebClient openIdWebClient;

  @Autowired
  private TokenService openIdTokenService;

  @Test
  void testContextLoads() {
    Assertions.assertNotNull(openIdWebClient);
  }

  @Test
  void testOpenIdClient_withGet_triggersDoOnConnected() {
    MockServerUtils.expectGet(ENDPOINT, 1, "Ok", HttpStatus.OK);

    StepVerifier.create(
            openIdWebClient.get()
                .uri(URL)
                .retrieve()
                .bodyToMono(String.class))
        .expectNextMatches(response -> response.equals("Ok"))
        .verifyComplete();
  }

  @Test
  void testTokenService_getServiceType_equalsOpenId() {
    Assertions.assertEquals(TOKEN_TYPE_BEARER, openIdTokenService.getTokenType());
  }
}
