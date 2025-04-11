package org.opentmf.client.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.opentmf.client.common.model.BaseClientProperties;
import org.opentmf.client.common.model.BaseClientProperties.Certificates;
import org.opentmf.client.common.model.BaseClientProperties.ProxyConfig;
import org.opentmf.client.config.test.ClientProperties;
import org.opentmf.client.config.test.TokenProperties;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

/**
 * @author Cezmi Aslan
 */
class BaseClientPropertiesTests {

  private static final int NUM_RETRIES = 0;
  private static final int REQUEST_TIME_MILLIS = 1000;
  private static final int RESPONSE_TIME_MILLIS = 1000;
  private static final int RETRY_WAIT_MILLIS = 1000;

  @Test
  void testBaseClientProperties_withExtensionProperties() {
    var clientProperties = buildClientProperties();
    clientProperties.setTokenConfig(buildTokenProperties());

    assertNotNull(clientProperties.getFixedHeaders());
    assertEquals(NUM_RETRIES, clientProperties.getNumRetries());
    assertEquals(REQUEST_TIME_MILLIS, clientProperties.getRequestTimeoutMillis());
    assertEquals(REQUEST_TIME_MILLIS, clientProperties.getResponseTimeoutMillis());
    assertEquals(RETRY_WAIT_MILLIS, clientProperties.getRetryWaitMillis());
  }

  @Test
  void testConfigurationMtlsAndProxy_throwsException()
      throws NoSuchMethodException, SecurityException {
    var clientProperties = buildClientProperties();
    clientProperties.setCertificates(new Certificates());
    clientProperties.setProxyConfig(new ProxyConfig());
    Method postConstruct =  BaseClientProperties.class.getDeclaredMethod("postConstruct");
    postConstruct.setAccessible(true);
    assertThrows(InvocationTargetException.class, () -> postConstruct.invoke(clientProperties));
  }

  private ClientProperties buildClientProperties() {
    var clientProperties = new ClientProperties();
    var fixedHeaders = new HashMap<String, String>();
    clientProperties.setFixedHeaders(fixedHeaders);
    clientProperties.setNumRetries(NUM_RETRIES);
    clientProperties.setRequestTimeoutMillis(REQUEST_TIME_MILLIS);
    clientProperties.setResponseTimeoutMillis(RESPONSE_TIME_MILLIS);
    clientProperties.setRetryWaitMillis(RETRY_WAIT_MILLIS);
    return clientProperties;
  }

  private TokenProperties buildTokenProperties() {
    return new TokenProperties();
  }
}
