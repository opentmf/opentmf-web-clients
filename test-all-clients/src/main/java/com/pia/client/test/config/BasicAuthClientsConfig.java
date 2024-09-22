package com.pia.client.test.config;

import com.pia.client.basic.model.BasicAuthClients;
import com.pia.client.basic.service.api.BasicTokenService;
import com.pia.client.basic.service.api.BasicWebClientProvider;
import lombok.RequiredArgsConstructor;
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
  public WebClient basicWebClient() {
    return basicWebClientProvider.buildWebClient(basicAuthClients.getBasic().get("client1"));
  }

  @Bean
  public BasicTokenService basicTokenService() {
    return basicWebClientProvider.buildTokenService(basicAuthClients.getBasic().get("client1"));
  }
}
