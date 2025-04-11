package org.opentmf.client.config.test;

import org.opentmf.client.common.model.BaseClientProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "client")
@Getter
@Setter
@Validated
public class ClientProperties extends BaseClientProperties {

  /**
   * Token properties of this client properties.
   */
  private TokenProperties tokenConfig;
}
