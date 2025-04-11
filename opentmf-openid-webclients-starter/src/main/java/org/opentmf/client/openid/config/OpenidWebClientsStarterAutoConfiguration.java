package org.opentmf.client.openid.config;

import org.opentmf.client.openid.model.OpenidClients;
import org.opentmf.client.openid.service.api.OpenidWebClientProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author Gokhan Demir
 */
@AutoConfiguration(after = OpenidWebClientProviderAutoConfiguration.class)
@EnableConfigurationProperties(OpenidClients.class)
public class OpenidWebClientsStarterAutoConfiguration {

  public OpenidWebClientsStarterAutoConfiguration(ConfigurableApplicationContext ctx,
      OpenidClients openidClients, OpenidWebClientProvider openidWebClientProvider) {

    var util = new OpenIdBeanRegistrationUtil(ctx.getBeanFactory(), openidWebClientProvider);
    openidClients.getOpenid().forEach(util::registerBeansIfNecessary);
  }

  /**
   * Marker bean to manage dependencies easily.
   *
   * @return a String with the value "openidWebClientsStarter".
   */
  @Bean
  public String openidWebClientsStarter() {
    return "openidWebClientsStarter";
  }
}
