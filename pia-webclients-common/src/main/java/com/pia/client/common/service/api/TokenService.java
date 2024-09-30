package com.pia.client.common.service.api;

import reactor.core.publisher.Mono;

/**
 * @author Gokhan Demir
 */
public interface TokenService {

  /**
   * Returns the type of the token this service provides, like Bearer, Basic, etc.
   * @return the type of the token this service provides, like Bearer, Basic, etc.
   */
  String getTokenType();

  /**
   * Returns a token from the configured client using baseUrl for the default token scope
   * provided in the configuration.
   *
   * @return a token from the configured client using baseUrl for the default token scope
   * provided in the configuration.
   */
  Mono<String> getToken();

  /**
   * Returns a token from the configured client baseUrl for the requested additionalScopes.
   *
   * @param additionalScopes The requested additionalScopes delimited by space if more than one
   *                         additional scope is requested. Null will be mapped to empty string.
   * @return a token from the configured client baseUrl for the requested additionalScopes.
   */
  Mono<String> getToken(String additionalScopes);
}
