package com.pia.client.basic.config;

import com.pia.client.basic.model.BasicAuthClients;
import com.pia.client.basic.service.api.BasicWebClientProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Gokhan Demir
 */
@AutoConfiguration(after = BasicWebClientProviderAutoConfiguration.class)
@EnableConfigurationProperties(BasicAuthClients.class)
public class BasicWebClientsStarterAutoConfiguration {

  public BasicWebClientsStarterAutoConfiguration(ConfigurableApplicationContext ctx,
      BasicAuthClients basicAuthClients,
      BasicWebClientProvider basicWebClientProvider) {
    var factory = ctx.getBeanFactory();
    for (var entry : basicAuthClients.getBasic().entrySet()) {
      var prefix = entry.getKey();
      var properties = entry.getValue();
      factory.registerSingleton(prefix + "ClientProperties", properties);
      factory.registerSingleton(prefix + "WebClient",
          basicWebClientProvider.buildWebClient(properties));
      factory.registerSingleton(prefix + "TokenService",
          basicWebClientProvider.buildTokenService(properties));
    }
  }
}
