package org.opentmf.client.basic.service.impl;

import static org.opentmf.client.common.util.WebClientConfigUtil.createWebClient;
import static org.opentmf.client.common.util.WebClientConfigUtil.httpClient;

import org.opentmf.client.basic.model.BasicClientProperties;
import org.opentmf.client.basic.service.api.BasicTokenService;
import org.opentmf.client.basic.service.api.BasicWebClientProvider;
import org.opentmf.client.common.service.impl.WebClientProviderBaseImpl;
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
