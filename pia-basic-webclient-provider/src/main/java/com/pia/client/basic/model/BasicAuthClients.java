package com.pia.client.basic.model;

import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Gokhan Demir
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "pia.webclient")
public class BasicAuthClients {

  /**
   * User defined arbitrary number of basicAuth client configurations.
   */
  @NotEmpty
  private Map<String, BasicClientProperties> basic;
}
