package org.opentmf.client.basic.service.impl;

import static org.opentmf.client.common.util.TokenUtil.TOKEN_TYPE_BASIC;
import static java.nio.charset.StandardCharsets.US_ASCII;

import org.opentmf.client.basic.model.BasicTokenProperties;
import org.opentmf.client.basic.service.api.BasicTokenService;
import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

/**
 * A no-op placeholder for token service for use with basic authentication.
 *
 * @author Gokhan Demir
 */
public class BasicTokenServiceImpl implements BasicTokenService {

  private final String token;

  public BasicTokenServiceImpl(BasicTokenProperties tokenProperties) {
    token = HttpHeaders.encodeBasicAuth(
        tokenProperties.getUsername(),
        tokenProperties.getPassword(),
        findCharset(tokenProperties.getCharset()));
  }

  @Override
  public String getTokenType() {
    return TOKEN_TYPE_BASIC;
  }

  @Override
  public Mono<String> getToken() {
    return Mono.just(token);
  }

  @Override
  public Mono<String> getToken(String additionalScopes) {
    return getToken();
  }

  private static Charset findCharset(String charset) {
    try {
      return Charset.forName(charset);
    } catch (Exception e) {
      return US_ASCII;
    }
  }
}
