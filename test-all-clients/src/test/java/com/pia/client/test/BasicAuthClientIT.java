package com.pia.client.test;

import com.pia.client.basic.model.BasicClientProperties;
import com.pia.client.basic.service.api.BasicTokenService;
import com.pia.client.basic.service.api.BasicWebClientProvider;
import com.pia.client.openid.model.OpenidClients;
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

  @Autowired private OpenidClients openidClients;
  @Autowired private BasicWebClientProvider basicWebClientProvider;

  @Autowired private BasicClientProperties firstBasicClientProperties;
  @Autowired private WebClient firstBasicWebClient;
  @Autowired private BasicTokenService firstBasicTokenService;

  @Autowired private BasicClientProperties secondBasicClientProperties;
  @Autowired private WebClient secondBasicWebClient;
  @Autowired private BasicTokenService secondBasicTokenService;

  @Test
  void testBasicAuth_withCorrectConfiguration_exposesBeans() {
    Assertions.assertNotNull(openidClients);
    Assertions.assertNotNull(firstBasicClientProperties);
    Assertions.assertNotNull(firstBasicWebClient);
    Assertions.assertNotNull(firstBasicTokenService);
    StepVerifier.create(firstBasicTokenService.getToken())
        .expectNextMatches(Objects::nonNull)
        .verifyComplete();

    Assertions.assertNotNull(secondBasicClientProperties);
    Assertions.assertNotNull(secondBasicWebClient);
    Assertions.assertNotNull(secondBasicTokenService);
    StepVerifier.create(secondBasicTokenService.getToken())
        .expectNextMatches(Objects::nonNull)
        .verifyComplete();

    Assertions.assertNotEquals(firstBasicClientProperties, secondBasicClientProperties);
    Assertions.assertNotEquals(firstBasicWebClient, secondBasicWebClient);
    Assertions.assertNotEquals(firstBasicTokenService, secondBasicTokenService);

    Assertions.assertEquals(firstBasicWebClient, basicWebClientProvider.buildWebClient(firstBasicClientProperties));
    Assertions.assertEquals(firstBasicTokenService, basicWebClientProvider.buildTokenService(firstBasicClientProperties));

    Assertions.assertEquals(secondBasicWebClient, basicWebClientProvider.buildWebClient(secondBasicClientProperties));
    Assertions.assertEquals(secondBasicTokenService, basicWebClientProvider.buildTokenService(secondBasicClientProperties));
  }
}
