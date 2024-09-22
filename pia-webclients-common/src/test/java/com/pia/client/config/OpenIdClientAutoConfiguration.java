package com.pia.client.config;

import static com.pia.client.common.util.TokenUtil.TOKEN_TYPE_BEARER;
import static com.pia.client.common.util.WebClientConfigUtil.createWebClient;
import static com.pia.client.common.util.WebClientConfigUtil.httpClient;

import com.pia.client.common.service.api.TokenService;
import com.pia.client.common.util.WebClientConfigUtil;
import com.pia.client.config.test.ClientProperties;
import java.util.Objects;
import javax.net.ssl.SSLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.autoconfigure.LogbookAutoConfiguration;
import reactor.core.publisher.Mono;
import reactor.netty.transport.ProxyProvider;

/**
 * OpenIdClientAutoConfiguration is responsible for configuring and creating the necessary beans for
 * the OpenId client. It configures the WebClient and creates a TokenService instance for accessing
 * OpenId token functionalities. It also handles SSL configuration, proxy settings, timeouts, and
 * other HTTP client settings.
 *
 * @author Yusuf BOZKURT
 */
@Slf4j
@AutoConfiguration
@AutoConfigureAfter(LogbookAutoConfiguration.class)
public class OpenIdClientAutoConfiguration {

  private final ClientProperties clientProperties;

  public OpenIdClientAutoConfiguration(ClientProperties clientProperties) {
    this.clientProperties = clientProperties;
  }

  /**
   * Creates and configures the WebClient for the ShClientAutoConfiguration.
   *
   * @param webClientBuilder Builder for the WebClient.
   * @param logbook          Logbook instance for logging.
   * @return Configured WebClient instance.
   * @throws SSLException If there's an issue with SSL configuration.
   */
  @Bean
  public WebClient openIdWebClient(WebClient.Builder webClientBuilder, Logbook logbook)
      throws SSLException {
    return buildOpenIdWebClient(webClientBuilder, logbook);
  }

  /**
   * Creates the TokenService using the configured WebClient.
   *
   * @param openIdWebClient Configured WebClient instance.
   * @return TokenService instance for accessing OpenIdToken functionalities.
   */
  @Bean
  public TokenService openIdTokenService(@Qualifier("openIdWebClient") WebClient openIdWebClient) {
    Assert.notNull(openIdWebClient, "openIdWebClient must not be null");
    return tokenServiceMock();
  }

  private TokenService tokenServiceMock() {
    return new TokenService() {
      @Override
      public String getTokenType() {
        return TOKEN_TYPE_BEARER;
      }

      @Override
      public Mono<String> getToken() {
        return Mono.just("token");
      }
    };
  }

  /**
   * Builds the web client for the ShClientAutoConfiguration. Configures the HTTP client with
   * provided settings including SSL, proxy, timeouts, etc.
   *
   * @param webClientBuilder Builder for the WebClient.
   * @param logbook          Logbook instance for logging.
   * @return Configured WebClient instance.
   * @throws SSLException If there's an issue with SSL configuration.
   */
  private WebClient buildOpenIdWebClient(WebClient.Builder webClientBuilder, Logbook logbook)
      throws SSLException {
    var httpClient = httpClient(logbook, clientProperties);
    if (Objects.nonNull(clientProperties.getProxyConfig())) {
      httpClient.proxy(this::proxy);
    }
    return createWebClient(webClientBuilder, httpClient, clientProperties);
  }

  private void proxy(ProxyProvider.TypeSpec typeSpec) {
    WebClientConfigUtil.proxy(typeSpec, clientProperties);
  }
}
