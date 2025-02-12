package com.pia.client.common.model;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
   * User defined <strong>unique</strong> name of the connection provider.
   * <p><strong>Heads Up:</strong> This value must be <span style="color:red; font-weight:bold">unique</span>
   * for each configured WebClient independent of the provider.
   * Providing unique names is the responsibility of the programmer and/or deployer (DevOps engineer).
   */
  @NotBlank
  private String connectionProviderName;

  /**
   * Maximum parallel connections.
   * @see ConnectionProvider#DEFAULT_POOL_MAX_CONNECTIONS
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

  /**
   * The keyStore and optionally the trustStore definitions. These will usually be read from
   * secrets.
   */
  private Certificates certificates;

  @Validated
  @Getter
  @Setter
  public static class Certificates {

    /**
    * The keyStore, that holds the private key and the received client certificate(s).
    */
    @NotNull
    KeyStore keyStore;

    /**
     * The keyStore, that holds the server certificates. If trustStore is not specified, Java VM's
     * cacerts file will be used by default.
     */
    TrustStore trustStore;

    @Validated
    @Getter
    @Setter
    public static class KeyStore {

      /**
       * The keyStore password.
       */
      String password;

      /**
       * The privateKey password that resides within this keyStore. This one is required. We do not support
       * key stores without a private key password.
       */
      @NotBlank
      String pkPassword;

      /**
       * The keyStore binary file contents in the form of a base-64 encoded string.
       */
      @NotBlank
      String base64Jks;
    }

    @Validated
    @Getter
    @Setter
    public static class TrustStore {

      /**
       * The keyStore password.
       */
      String password;

      /**
       * The keyStore binary file contents in the form of a base-64 encoded string.
       */
      @NotBlank
      String base64Jks;
    }

  }

  @PostConstruct
  private void postConstruct() {
    if (certificates != null && proxyConfig != null) {
      throw new IllegalArgumentException(
          "Proxy is not supported for mutual TLS connection.");
    }
  }
}