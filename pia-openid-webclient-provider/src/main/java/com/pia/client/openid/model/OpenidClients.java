package com.pia.client.openid.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * @author Gokhan Demir
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "pia.webclient")
@Validated
public class OpenidClients {

  /**
   * User defined arbitrary number of OpenID client configurations.
   */
  @NotEmpty
  private Map<@NotEmpty String, @Valid OpenidClientProperties> openid;
}
