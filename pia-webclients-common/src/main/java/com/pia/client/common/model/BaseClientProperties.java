package com.pia.client.common.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import reactor.netty.resources.ConnectionProvider;

/**
 * @author Gokhan Demir
 */
@Validated
@Getter
@Setter
public abstract class BaseClientProperties {

  /**
   * User defined name of the connection provider.
   */
  @NotBlank
  private String connectionProviderName;

  /**
   * Maximum parallel connections
   */
  @Positive
  private int maxConnections = ConnectionProvider.DEFAULT_POOL_MAX_CONNECTIONS;

  /**
   * Request timeout duration in terms of milliseconds.
   */
  @Positive
  private int requestTimeoutMillis = 30000;

  /**
   * Response timeout duration in terms of milliseconds.
   */
  @Positive
  private long responseTimeoutMillis = 45000;

  /**
   * Retry count for failed and retryable errors.
   */
  @PositiveOrZero
  private int numRetries = 3;

  /**
   * Wait duration between each retry in terms of milliseconds.
   */
  @Positive
  private long retryWaitMillis = 5000;

  /**
   * Optional map of header name and static header value. When specified, these headers will
   * automatically be set in the configured WebClient.
   */
  private Map<String, String> fixedHeaders;

  /**
   * Configuration for the proxy settings if {@code useProxy} is set to true.
   */
  private ProxyConfig proxyConfig;

  /**
   * Represents the configuration for the proxy settings.
   */
  @Validated
  @Getter
  @Setter
  public static class ProxyConfig {

    /**
     * Hostname or IP address of the proxy server.
     */
    @NotBlank
    private String proxyHost;

    /**
     * Port number of the proxy server.
     */
    @Positive
    private int proxyPort;

    /**
     * List of non-proxy hosts.
     */
    private List<String> nonProxyHosts;
  }
}
