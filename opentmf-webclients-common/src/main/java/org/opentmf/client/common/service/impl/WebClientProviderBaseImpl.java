package org.opentmf.client.common.service.impl;

import org.opentmf.client.common.model.BaseClientProperties;
import org.opentmf.client.common.service.api.TokenService;
import org.opentmf.client.common.service.api.WebClientProvider;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implements local caching of webClient and tokenService.
 *
 * @param <P> a class that extends BaseClientProperties.
 * @param <T> a class that extends TokenService.
 * @author Gokhan Demir
 */
public abstract class WebClientProviderBaseImpl<P extends BaseClientProperties, T extends TokenService>
    implements WebClientProvider<P, T> {

  private final Map<String, WebClient> webClientCache = new HashMap<>();
  private final Map<String, T> tokenServiceCache = new HashMap<>();

  private static final String TOKEN_SERVICE_PREFIX = "TokenService_";
  private static final String WEB_CLIENT_PREFIX = "WebClient_";

  protected abstract WebClient buildWebClientFromScratch(P properties);
  protected abstract T buildTokenServiceFromScratch(P properties);

  @Override
  public WebClient buildWebClient(P properties) {
    var webClient = getWebClientFromLocalCache(properties);
    if (webClient == null) {
      webClient = buildWebClientFromScratch(properties);
      putWebClientInLocalCache(properties, webClient);
    }
    return webClient;
  }

  @Override
  public T buildTokenService(P properties) {
    T tokenService = getTokenServiceFromLocalCache(properties);
    if (tokenService == null) {
      tokenService = buildTokenServiceFromScratch(properties);
      putTokenServiceInLocalCache(properties, tokenService);
    }
    return tokenService;
  }

  private WebClient getWebClientFromLocalCache(P properties) {
    return webClientCache.get(WEB_CLIENT_PREFIX + properties.getConnectionProviderName());
  }

  private void putWebClientInLocalCache(P properties, WebClient webClient) {
    webClientCache.put(WEB_CLIENT_PREFIX + properties.getConnectionProviderName(), webClient);
  }

  private T getTokenServiceFromLocalCache(P properties) {
    return tokenServiceCache.get(TOKEN_SERVICE_PREFIX + properties.getConnectionProviderName());
  }

  private void putTokenServiceInLocalCache(P properties, T tokenService) {
    tokenServiceCache.put(TOKEN_SERVICE_PREFIX + properties.getConnectionProviderName(), tokenService);
  }
}
