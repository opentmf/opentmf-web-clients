package com.pia.client.basic.service.impl;

import static com.pia.client.common.util.WebClientConfigUtil.createWebClient;
import static com.pia.client.common.util.WebClientConfigUtil.httpClient;

import com.pia.client.basic.model.BasicClientProperties;
import com.pia.client.basic.service.api.BasicTokenService;
import com.pia.client.basic.service.api.BasicWebClientProvider;
import com.pia.client.common.service.impl.WebClientProviderBaseImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.zalando.logbook.Logbook;

/**
 * @author Gokhan Demir
 */
@RequiredArgsConstructor
public class BasicWebClientProviderImpl
    extends WebClientProviderBaseImpl<BasicClientProperties, BasicTokenService>
    implements BasicWebClientProvider {

  private final WebClient.Builder webClientBuilder;
  private final Logbook logbook;

  @Override
  protected WebClient buildWebClientFromScratch(BasicClientProperties properties) {
    try {
      var httpClient = httpClient(logbook, properties);
      return createWebClient(webClientBuilder, httpClient, properties);
    } catch (Exception e) {
      throw new IllegalArgumentException("Can't create webClient using the properties", e);
    }
  }

  @Override
  protected BasicTokenService buildTokenServiceFromScratch(BasicClientProperties properties) {
    return new BasicTokenServiceImpl(properties.getTokenConfig());
  }
}
