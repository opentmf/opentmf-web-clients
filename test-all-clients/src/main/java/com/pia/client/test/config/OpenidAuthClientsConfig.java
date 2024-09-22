package com.pia.client.test.config;

import com.pia.client.openid.model.OpenidClients;
import com.pia.client.openid.service.api.OpenidTokenService;
import com.pia.client.openid.service.api.OpenidWebClientProvider;
import lombok.RequiredArgsConstructor;
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
  public WebClient openidWebClient() {
    return openidWebClientProvider.buildWebClient(openidClients.getOpenid().get("client1"));
  }

  @Bean
  public OpenidTokenService openidTokenService() {
    return openidWebClientProvider.buildTokenService(openidClients.getOpenid().get("client1"));
  }
}
