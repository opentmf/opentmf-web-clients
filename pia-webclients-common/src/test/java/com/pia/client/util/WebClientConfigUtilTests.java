package com.pia.client.util;

import static com.pia.client.common.util.WebClientConfigUtil.createWebClient;

import com.pia.client.common.model.BaseClientProperties.ProxyConfig;
import com.pia.client.common.util.WebClientConfigUtil;
import com.pia.client.config.test.ClientProperties;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.zalando.logbook.Logbook;
import reactor.netty.transport.ProxyProvider;

/**
 * @author Gokhan Demir
 */
@ExtendWith(MockitoExtension.class)
class WebClientConfigUtilTests {

  @Mock private Logbook logbook;

  @Test
  void testBuildHttpClient() {
    Assertions.assertDoesNotThrow(() -> {
      WebClient.Builder builder = WebClient.builder();
      var clientProperties = getClientProperties();
      var httpClient = WebClientConfigUtil.httpClient(logbook, clientProperties);
      if (Objects.nonNull(clientProperties.getProxyConfig())) {
        httpClient
            .responseTimeout(Duration.ofMillis(clientProperties.getResponseTimeoutMillis()))
            .proxy(this::proxy);
      }
      createWebClient(builder, httpClient, clientProperties);
    });
  }

  private void proxy(ProxyProvider.TypeSpec typeSpec) {
    WebClientConfigUtil.proxy(typeSpec, getClientProperties());
  }

  private ClientProperties getClientProperties() {
    var properties = new ClientProperties();
    properties.setProxyConfig(getProxyConfig());
    properties.setFixedHeaders(Map.of("header1", "value1"));
    properties.setConnectionProviderName("connection-provider-name");
    return properties;
  }

  private ProxyConfig getProxyConfig() {
    var proxyConfig = new ProxyConfig();
    proxyConfig.setProxyHost("localhost");
    proxyConfig.setProxyPort(3128);
    proxyConfig.setNonProxyHosts(List.of("host1", "host2", "host3"));
    return proxyConfig;
  }
}
