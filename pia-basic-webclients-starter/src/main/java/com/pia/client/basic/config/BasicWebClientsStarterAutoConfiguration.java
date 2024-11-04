package com.pia.client.basic.config;

import com.pia.client.basic.model.BasicAuthClients;
import com.pia.client.basic.service.api.BasicWebClientProvider;
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
      BasicAuthClients basicAuthClients,
      BasicWebClientProvider basicWebClientProvider) {
    var factory = ctx.getBeanFactory();
    log.debug("Auto configuring BasicWebClientsStarter");
    for (var entry : basicAuthClients.getBasic().entrySet()) {
      var prefix = entry.getKey();
      var properties = entry.getValue();
      factory.registerSingleton(prefix + "ClientProperties", properties);
      factory.registerSingleton(prefix + "WebClient",
          basicWebClientProvider.buildWebClient(properties));
      factory.registerSingleton(prefix + "TokenService",
          basicWebClientProvider.buildTokenService(properties));
      log.debug("Exposed: {} basic auth beans for WebClient, ClientProperties, and TokenService.", prefix);
    }
  }

  @Bean
  public String basicWebClientsStarter() {
    return "basicWebClientsStarter";
  }
}
