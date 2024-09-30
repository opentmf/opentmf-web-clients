package com.pia.client.openid.service.api;

import com.pia.client.common.service.api.TokenService;
import java.net.URI;
import java.util.Map;
import reactor.core.publisher.Mono;

/**
 * @author Gokhan Demir
 */
public interface OpenidTokenService extends TokenService {

  /**
   * Returns a token to be retrieved from the requested baseUrl for the requested additionalScopes.
   * <p>
   * This method is useful in situations where we want to get a token from a different
   * authentication server rather than the one configured as baseUrl. Token baseUrl may be different
   * than client baseUrl.
   * </p>
   *
   * @param tokenUri         the requested authentication server tokenUri.
   * @param additionalScopes The requested additionalScopes delimited by space if more than one
   *                         additional scope is requested. Null will be mapped to empty string.
   * @return a token to be retrieved from the requested baseUrl for the requested additionalScopes.
   */
  Mono<String> getToken(URI tokenUri, String additionalScopes);

  /**
   * Returns an access_token after adding/overriding the statically configured form-data using the
   * provided map.
   *
   * @param enricher the key - value pairs to add/override in the configured formData.
   * @return the access_token after overriding the form data.
   */
  Mono<String> getToken(Map<String, String> enricher);

  /**
   * Clears the token cache.
   * <p>
   * This method can be useful for testing purposes.
   * </p>
   */
  void clearCache();
}
