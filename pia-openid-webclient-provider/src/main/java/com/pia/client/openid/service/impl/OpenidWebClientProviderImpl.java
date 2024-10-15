package com.pia.client.openid.service.impl;

import static com.pia.client.common.util.TokenUtil.CACHING_PROVIDER;
import static com.pia.client.common.util.WebClientConfigUtil.createWebClient;
import static com.pia.client.common.util.WebClientConfigUtil.httpClient;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pia.client.common.service.impl.WebClientProviderBaseImpl;
import com.pia.client.openid.model.OpenidClientProperties;
import com.pia.client.openid.service.api.OpenidTokenService;
import com.pia.client.openid.service.api.OpenidTokenServiceMockImpl;
import com.pia.client.openid.service.api.OpenidWebClientProvider;
import java.util.concurrent.TimeUnit;
import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.zalando.logbook.Logbook;

/**
 * @author Gokhan Demir
 */
@RequiredArgsConstructor
public class OpenidWebClientProviderImpl
    extends WebClientProviderBaseImpl<OpenidClientProperties, OpenidTokenService>
    implements OpenidWebClientProvider {

  private final WebClient.Builder webClientBuilder;
  private final Logbook logbook;

  @Override
  protected WebClient buildWebClientFromScratch(OpenidClientProperties properties) {
    try {
      var httpClient = httpClient(logbook, properties);
      return createWebClient(webClientBuilder, httpClient, properties);
    } catch (Exception e) {
      throw new IllegalArgumentException("Can't create webClient using the properties", e);
    }
  }

  @Override
  protected OpenidTokenService buildTokenServiceFromScratch(OpenidClientProperties properties) {
    if (properties.getTokenConfig().isUseMock()) {
      return new OpenidTokenServiceMockImpl();
    } else {
      var tokenClient = new OpenidTokenClientImpl(properties, buildWebClient(properties));
      return new OpenidTokenServiceImpl(properties.getTokenConfig(),
          createAccessTokenCache(properties), tokenClient);
    }
  }

  private Cache<String, ObjectNode> createAccessTokenCache(OpenidClientProperties properties) {
    var expiryDuration = new Duration(TimeUnit.SECONDS,
        properties.getTokenConfig().getCacheExpirySeconds());

    var config = new MutableConfiguration<String, ObjectNode>()
        .setTypes(String.class, ObjectNode.class)
        .setExpiryPolicyFactory(() -> new CreatedExpiryPolicy(expiryDuration))
        .setStoreByValue(true);

    var cachingProvider = Caching.getCachingProvider(CACHING_PROVIDER);

    return cachingProvider.getCacheManager()
        .createCache(properties.getConnectionProviderName(), config);
  }
}
