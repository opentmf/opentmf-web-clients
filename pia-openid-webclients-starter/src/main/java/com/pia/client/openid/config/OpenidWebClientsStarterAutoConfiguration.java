package com.pia.client.openid.config;

import com.pia.client.openid.model.OpenidClients;
import com.pia.client.openid.service.api.OpenidWebClientProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Gokhan Demir
 */
@AutoConfiguration(after = OpenidWebClientProviderAutoConfiguration.class)
@EnableConfigurationProperties(OpenidClients.class)
public class OpenidWebClientsStarterAutoConfiguration {

  public OpenidWebClientsStarterAutoConfiguration(ConfigurableApplicationContext ctx,
      OpenidClients openidClients,
      OpenidWebClientProvider openidWebClientProvider) {
    var factory = ctx.getBeanFactory();
    for (var entry : openidClients.getOpenid().entrySet()) {
      var prefix = entry.getKey();
      var properties = entry.getValue();
      factory.registerSingleton(prefix + "ClientProperties", properties);
      factory.registerSingleton(prefix + "WebClient",
          openidWebClientProvider.buildWebClient(properties));
      factory.registerSingleton(prefix + "TokenService",
          openidWebClientProvider.buildTokenService(properties));
    }
  }
}
