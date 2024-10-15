package com.pia.client.test.config;

import com.pia.client.basic.model.BasicAuthClients;
import com.pia.client.basic.model.BasicClientProperties;
import com.pia.client.basic.service.api.BasicTokenService;
import com.pia.client.basic.service.api.BasicWebClientProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Gokhan Demir
 */
@Configuration
@RequiredArgsConstructor
public class BasicAuthClientsConfig {

  private final BasicWebClientProvider basicWebClientProvider;
  private final BasicAuthClients basicAuthClients;

  @Bean
  public BasicClientProperties firstBasicClientProperties() {
    return basicAuthClients.getBasic().get("firstBasic");
  }

  @Bean
  public WebClient firstBasicWebClient(
      @Qualifier("firstBasicClientProperties") BasicClientProperties firstBasicClientProperties) {
    return basicWebClientProvider.buildWebClient(firstBasicClientProperties);
  }

  @Bean
  public BasicTokenService firstBasicTokenService(
      @Qualifier("firstBasicClientProperties") BasicClientProperties firstBasicClientProperties) {
    return basicWebClientProvider.buildTokenService(firstBasicClientProperties);
  }

  @Bean
  public BasicClientProperties secondBasicClientProperties() {
    return basicAuthClients.getBasic().get("secondBasic");
  }

  @Bean
  public WebClient secondBasicWebClient(
      @Qualifier("secondBasicClientProperties") BasicClientProperties secondBasicClientProperties) {
    return basicWebClientProvider.buildWebClient(secondBasicClientProperties);
  }

  @Bean
  public BasicTokenService secondBasicTokenService(
      @Qualifier("secondBasicClientProperties") BasicClientProperties secondBasicClientProperties) {
    return basicWebClientProvider.buildTokenService(secondBasicClientProperties);
  }
}
