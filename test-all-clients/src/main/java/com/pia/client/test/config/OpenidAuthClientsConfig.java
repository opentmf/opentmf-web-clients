package com.pia.client.test.config;

import com.pia.client.openid.model.OpenidClientProperties;
import com.pia.client.openid.model.OpenidClients;
import com.pia.client.openid.service.api.OpenidTokenService;
import com.pia.client.openid.service.api.OpenidWebClientProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Gokhan Demir
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(OpenidClients.class)
public class OpenidAuthClientsConfig {

  private final OpenidWebClientProvider openidWebClientProvider;
  private final OpenidClients openidClients;

  @Bean
  public OpenidClientProperties firstOpenIdClientProperties() {
    return openidClients.getOpenid().get("firstOpenId");
  }

  @Bean
  public WebClient firstOpenIdWebClient(
      @Qualifier("firstOpenIdClientProperties") OpenidClientProperties firstOpenIdClientProperties) {
    return openidWebClientProvider.buildWebClient(firstOpenIdClientProperties);
  }

  @Bean
  public OpenidTokenService firstOpenIdTokenService(
      @Qualifier("firstOpenIdClientProperties") OpenidClientProperties firstOpenIdClientProperties) {
    return openidWebClientProvider.buildTokenService(firstOpenIdClientProperties);
  }

  @Bean
  public OpenidClientProperties secondOpenIdClientProperties() {
    return openidClients.getOpenid().get("secondOpenId");
  }

  @Bean
  public WebClient secondOpenIdWebClient(
      @Qualifier("secondOpenIdClientProperties") OpenidClientProperties secondOpenIdClientProperties) {
    return openidWebClientProvider.buildWebClient(secondOpenIdClientProperties);
  }

  @Bean
  public OpenidTokenService secondOpenIdTokenService(
      @Qualifier("secondOpenIdClientProperties") OpenidClientProperties secondOpenIdClientProperties) {
    return openidWebClientProvider.buildTokenService(secondOpenIdClientProperties);
  }
}
