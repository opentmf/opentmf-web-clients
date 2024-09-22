package com.pia.client.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.pia.client.config.test.ClientProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("junit")
class BaseClientPropertiesIT {

  @Autowired
  private ClientProperties clientProperties;

  @Test
  void testBaseClientProperties_withExtensionProperties_loadsFromPropertiesFile() {
    assertFalse(clientProperties.getTokenConfig().isUseMock());
    assertEquals(500, clientProperties.getMaxConnections());
  }
}
