package com.pia.client.common.util;

import com.pia.client.common.model.BaseClientProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.time.Duration;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import lombok.Generated;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.netty.LogbookClientHandler;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.ProxyProvider;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

/**
 * Common methods when building a web client.
 *
 * @author Gokhan Demir
 * @author Cezmi Aslan for mTls support
 */
public final class WebClientConfigUtil {

  @Generated
  private WebClientConfigUtil() {
  }

  private static final int MAX_IDLE_TIME_MINUTES = 4;
  private static final int MAX_IN_MEMORY = 16 * 1024 * 1024;

  public static HttpClient httpClient(
      Logbook logbook,
      BaseClientProperties clientProperties) throws SSLException {
    var sslContext = buildSslContext(clientProperties);
    var httpClient = httpClient(logbook, sslContext, clientProperties);

    if (clientProperties.getProxyConfig() != null) {
      httpClient.proxy(typeSpec -> WebClientConfigUtil.proxy(typeSpec, clientProperties));
    }
    return httpClient;
  }

  private static HttpClient httpClient(
      Logbook logbook,
      SslContext sslContext,
      BaseClientProperties clientProperties) {
    var connectionProvider = buildConnectionProvider(clientProperties);
    return HttpClient.create(connectionProvider)
        .wiretap(HttpClient.class.getName(), LogLevel.INFO, AdvancedByteBufFormat.SIMPLE)
        .compress(true)
        .responseTimeout(Duration.ofMillis(clientProperties.getResponseTimeoutMillis()))
        .keepAlive(true)
        .secure(spec ->
            spec.sslContext(sslContext).handshakeTimeout(
                Duration.ofMillis(clientProperties.getResponseTimeoutMillis())))
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, clientProperties.getRequestTimeoutMillis())
        .option(ChannelOption.SO_KEEPALIVE, true)
        .doOnConnected(connection -> doOnConnected(connection, logbook, clientProperties));
  }

  public static WebClient createWebClient(WebClient.Builder webClientBuilder, HttpClient httpClient,
      BaseClientProperties clientProperties) {
    return webClientBuilder.defaultHeaders((HttpHeaders httpHeaders) -> {
          httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
          if (!CollectionUtils.isEmpty(clientProperties.getFixedHeaders())) {
            clientProperties.getFixedHeaders().forEach(httpHeaders::add);
          }
        })
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .exchangeStrategies(buildExchangeStrategies())
        .build();
  }

  private static ExchangeStrategies buildExchangeStrategies() {
    return ExchangeStrategies.builder()
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY))
        .build();
  }

  private static SslContext buildSslContext(BaseClientProperties clientProperties) throws SSLException {
    if (clientProperties.getCertificates() != null) {
      return buildMtlsSslContext(clientProperties);
    }
    return buildGenericSslContext();
  }

  private static SslContext buildMtlsSslContext(BaseClientProperties clientProperties) {
    try {
      var keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      var keyCert = Base64.getDecoder().decode(
          clientProperties.getCertificates().getKeyStore().getBase64Jks());
      var keyStorePassword = clientProperties.getCertificates().getKeyStore().getPassword();
      var keyManagerFactory = KeyManagerFactory.getInstance("SunX509");

      keyStore.load(new ByteArrayInputStream(keyCert),
          keyStorePassword == null ? null : keyStorePassword.toCharArray());
      keyManagerFactory.init(keyStore,
          clientProperties.getCertificates().getKeyStore().getPkPassword().toCharArray());

      if (clientProperties.getCertificates().getTrustStore() == null) {
        return SslContextBuilder.forClient().keyManager(keyManagerFactory).build();
      }

      var trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
      var trustCert = Base64.getDecoder().decode(
          clientProperties.getCertificates().getTrustStore().getBase64Jks());
      var trustStorePassword = clientProperties.getCertificates().getTrustStore().getPassword();
      var trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
      trustStore.load(
          new ByteArrayInputStream(trustCert),
          trustStorePassword == null ? null : trustStorePassword.toCharArray());
      trustManagerFactory.init(trustStore);
      return SslContextBuilder.forClient().keyManager(keyManagerFactory)
          .trustManager(trustManagerFactory).build();
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Error creating 2-Way TLS WebClient. Check key-store and trust-store.", e);
    }
  }

  private static SslContext buildGenericSslContext() throws SSLException {
    return SslContextBuilder.forClient()
        .trustManager(InsecureTrustManagerFactory.INSTANCE)
        .build();
  }

  private static ConnectionProvider buildConnectionProvider(
      BaseClientProperties clientProperties) {
    return ConnectionProvider.builder(clientProperties.getConnectionProviderName())
        .maxIdleTime(Duration.ofMinutes(MAX_IDLE_TIME_MINUTES))
        .maxConnections(clientProperties.getMaxConnections())
        .metrics(true)
        .build();
  }

  private static void doOnConnected(Connection conn, Logbook logbook,
      BaseClientProperties clientProperties) {
    var requestTimeoutMillis = clientProperties.getRequestTimeoutMillis();
    var responseTimeoutMillis = clientProperties.getResponseTimeoutMillis();
    conn.addHandlerLast(new ReadTimeoutHandler(responseTimeoutMillis, TimeUnit.MILLISECONDS))
        .addHandlerLast(new WriteTimeoutHandler(requestTimeoutMillis, TimeUnit.MILLISECONDS))
        .addHandlerLast(new LogbookClientHandler(logbook));
  }

  public static void proxy(ProxyProvider.TypeSpec typeSpec,
      BaseClientProperties clientProperties) {
    var proxyConfig = Objects.requireNonNull(clientProperties.getProxyConfig(), "ProxyConfig cannot be null");
    typeSpec.type(ProxyProvider.Proxy.HTTP)
        .address(InetSocketAddress.createUnresolved(proxyConfig.getProxyHost(), proxyConfig.getProxyPort()))
        .connectTimeoutMillis(clientProperties.getResponseTimeoutMillis())
        .nonProxyHosts(nonProxyHostsPattern(clientProperties));
  }

  private static String nonProxyHostsPattern(BaseClientProperties clientProperties) {
    var proxyConfig = Objects.requireNonNull(clientProperties.getProxyConfig(), "ProxyConfig cannot be null");
    if (CollectionUtils.isEmpty(proxyConfig.getNonProxyHosts())) {
      return "";
    }
    return StringUtils.collectionToDelimitedString(proxyConfig.getNonProxyHosts(), "|");
  }
}
