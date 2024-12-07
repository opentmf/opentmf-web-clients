package com.pia.client.common.util;

import static org.springframework.util.StringUtils.hasText;

import java.net.URI;
import lombok.Generated;

/**
 * @author Gokhan Demir
 */
public class TokenUtil {

  @Generated
  private TokenUtil() {
  }

  public static final String TOKEN_TYPE_BEARER = "Bearer";
  public static final String TOKEN_TYPE_BASIC = "Basic";

  public static final String CACHING_PROVIDER = "org.ehcache.jsr107.EhcacheCachingProvider";

  public static final String CLIENT_PROPERTIES = "ClientProperties";
  public static final String WEB_CLIENT = "WebClient";
  public static final String TOKEN_SERVICE = "TokenService";

  public static String cacheKey(URI baseUrl, String scope, String username) {
    return ((username + " " + scope).trim() + " " + baseUrl).trim();
  }

  public static String firstNonNull(String... parameters) {
    for (String parameter : parameters) {
      if (hasText(parameter)) {
        return parameter;
      }
    }
    return null;
  }
}
