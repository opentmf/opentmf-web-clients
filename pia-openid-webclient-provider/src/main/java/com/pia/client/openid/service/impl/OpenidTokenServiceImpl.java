package com.pia.client.openid.service.impl;

import static com.pia.client.common.util.TokenUtil.TOKEN_TYPE_BEARER;
import static com.pia.client.common.util.TokenUtil.cacheKey;
import static com.pia.client.openid.util.OpenidTokenUtil.SCOPE;
import static com.pia.client.openid.util.OpenidTokenUtil.findScope;
import static com.pia.client.openid.util.OpenidTokenUtil.findUsername;
import static java.util.Collections.emptyMap;
import static org.springframework.util.StringUtils.hasText;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pia.client.openid.model.OpenidTokenProperties;
import com.pia.client.openid.service.api.OpenidTokenClient;
import com.pia.client.openid.service.api.OpenidTokenService;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

/**
 * @author Gokhan Demir
 */
@Slf4j
@RequiredArgsConstructor
public class OpenidTokenServiceImpl implements OpenidTokenService {

  private final OpenidTokenProperties properties;
  private final Cache<String, ObjectNode> tokenCache;
  private final OpenidTokenClient openidTokenClient;

  @Override
  public String getTokenType() {
    return TOKEN_TYPE_BEARER;
  }

  @Override
  public Mono<String> getToken() {
    return getToken(properties.getTokenUrl(), null, emptyMap());
  }

  @Override
  public Mono<String> getToken(String additionalScopes) {
    return getToken(properties.getTokenUrl(), additionalScopes, emptyMap());
  }

  @Override
  public Mono<String> getToken(URI tokenUri, String additionalScopes) {
    return getToken(tokenUri, additionalScopes, emptyMap());
  }

  @Override
  public Mono<String> getToken(Map<String, String> enricher) {
    return getToken(properties.getTokenUrl(), null, enricher == null ? emptyMap() : enricher);
  }

  @Override
  public void clearCache() {
    tokenCache.clear();
  }

  private Mono<String> getToken(URI uri, String additionalScopes, Map<String, String> enricher) {
    var scope = findScope(additionalScopes, properties.getFormData().get(SCOPE), enricher);
    var username = findUsername(properties, enricher);
    var key = cacheKey(uri, scope, username);
    var tokenObject = tokenCache.get(key);
    if (tokenObject != null) {
      log.trace("Returning cached openid token for baseUrl: {}, scope: {}, username: {}",
          uri, scope, username);
      return Mono.just(extractToken(tokenObject));
    }
    var multiValueMap = enrich(username, scope, enricher);
    return openidTokenClient.retrieveToken(uri, multiValueMap)
        .doOnNext(jsonObjectNode -> tokenCache.put(key, jsonObjectNode))
        .map(this::extractToken);
  }

  private String extractToken(ObjectNode objectNode) {
    return objectNode.get(properties.getTokenField()).textValue();
  }

  private MultiValueMap<String, String> enrich(String username, String scope,
      Map<String, String> enricher) {
    var map = new HashMap<>(properties.getFormData());
    map.putAll(enricher);
    if (hasText(username)) {
      map.put(properties.getUsernameField(), username);
    }
    if (hasText(scope)) {
      map.put(SCOPE, scope);
    }
    return toMultiValueMap(map);
  }

  private MultiValueMap<String, String> toMultiValueMap(Map<String, String> map) {
    var linkedMap = new LinkedMultiValueMap<String, String>();
    map.forEach(linkedMap::add);
    return linkedMap;
  }
}
