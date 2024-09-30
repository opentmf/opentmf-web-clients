package com.pia.client.basic.service.impl;

import static com.pia.client.common.util.WebClientConfigUtil.createWebClient;
import static com.pia.client.common.util.WebClientConfigUtil.httpClient;

import com.pia.client.basic.model.BasicClientProperties;
import com.pia.client.basic.service.api.BasicTokenService;
import com.pia.client.basic.service.api.BasicWebClientProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.zalando.logbook.Logbook;

/**
 * @author Gokhan Demir
 */
@RequiredArgsConstructor
public class BasicWebClientProviderImpl implements BasicWebClientProvider {

  private final WebClient.Builder webClientBuilder;
  private final Logbook logbook;

  private WebClient webClient = null;
  private BasicTokenService tokenService = null;

  @Override
  public WebClient buildWebClient(BasicClientProperties properties) {
    if (webClient == null) {
      try {
        var httpClient = httpClient(logbook, properties);
        webClient = createWebClient(webClientBuilder, httpClient, properties);
      } catch (Exception e) {
        throw new IllegalArgumentException("Can't create webClient using the properties", e);
      }
    }
    return webClient;
  }

  @Override
  public BasicTokenService buildTokenService(BasicClientProperties properties) {
    if (tokenService == null) {
      tokenService = new BasicTokenServiceImpl(properties.getTokenConfig());
    }
    return tokenService;
  }
}
