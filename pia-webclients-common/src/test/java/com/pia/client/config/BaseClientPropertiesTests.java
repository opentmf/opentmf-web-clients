package com.pia.client.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.pia.client.config.test.ClientProperties;
import com.pia.client.config.test.TokenProperties;
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
