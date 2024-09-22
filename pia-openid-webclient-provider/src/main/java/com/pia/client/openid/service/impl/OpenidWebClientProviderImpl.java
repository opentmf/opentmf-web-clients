package com.pia.client.openid.service.impl;

import static com.pia.client.common.util.TokenUtil.CACHING_PROVIDER;
import static com.pia.client.common.util.WebClientConfigUtil.createWebClient;
import static com.pia.client.common.util.WebClientConfigUtil.httpClient;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pia.client.openid.model.OpenidClientProperties;
import com.pia.client.openid.model.OpenidTokenProperties;
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
public class OpenidWebClientProviderImpl implements OpenidWebClientProvider {

  private final WebClient.Builder webClientBuilder;
  private final Logbook logbook;

  private WebClient webClient = null;
  private Cache<String, ObjectNode> accessTokenCache = null;
  private OpenidTokenService tokenService = null;

  @Override
  public WebClient buildWebClient(OpenidClientProperties properties) {
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
  public OpenidTokenService buildTokenService(OpenidClientProperties properties) {
    if (tokenService == null) {
      if (properties.getTokenConfig().isUseMock()) {
        tokenService = new OpenidTokenServiceMockImpl();
      } else {
        var client = new OpenidTokenClientImpl(properties, buildWebClient(properties));
        tokenService = new OpenidTokenServiceImpl(properties.getTokenConfig(),
            getCache(properties.getTokenConfig()), client);
      }
    }
    return tokenService;
  }

  private Cache<String, ObjectNode> getCache(OpenidTokenProperties properties) {
    if (accessTokenCache == null) {

      var expiryDuration = new Duration(TimeUnit.SECONDS, properties.getCacheExpirySeconds());
      var config = new MutableConfiguration<String, ObjectNode>()
          .setTypes(String.class, ObjectNode.class)
          .setExpiryPolicyFactory(() -> new CreatedExpiryPolicy(expiryDuration))
          .setStoreByValue(true);

      var cachingProvider = Caching.getCachingProvider(CACHING_PROVIDER);

      accessTokenCache = cachingProvider.getCacheManager()
          .createCache(properties.getCacheName(), config);
    }
    return accessTokenCache;
  }
}
