package org.opentmf.client.basic.config;

import org.opentmf.client.basic.model.BasicAuthClients;
import org.opentmf.client.basic.service.api.BasicWebClientProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author Gokhan Demir
 */
@AutoConfiguration(after = BasicWebClientProviderAutoConfiguration.class)
@EnableConfigurationProperties(BasicAuthClients.class)
@Slf4j
public class BasicWebClientsStarterAutoConfiguration {

  public BasicWebClientsStarterAutoConfiguration(ConfigurableApplicationContext ctx,
      BasicAuthClients basicAuthClients, BasicWebClientProvider basicWebClientProvider) {

    var util = new BasicBeanRegistrationUtil(ctx.getBeanFactory(), basicWebClientProvider);
    basicAuthClients.getBasic().forEach(util::registerBeansIfNecessary);
  }

  /**
   * Marker bean to manage dependencies easily.
   *
   * @return a String with the value "basicWebClientsStarter".
   */
  @Bean
  public String basicWebClientsStarter() {
    return "basicWebClientsStarter";
  }
}
