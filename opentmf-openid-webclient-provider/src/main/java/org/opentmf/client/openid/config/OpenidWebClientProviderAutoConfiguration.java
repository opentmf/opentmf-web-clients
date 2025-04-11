package org.opentmf.client.openid.config;

import org.opentmf.client.openid.service.api.OpenidWebClientProvider;
import org.opentmf.client.openid.service.impl.OpenidWebClientProviderImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.autoconfigure.LogbookAutoConfiguration;

/**
 * Exposes the OpenidWebClientProvider bean.
 *
 * @author Gokhan Demir
 */
@AutoConfiguration(after = LogbookAutoConfiguration.class)
public class OpenidWebClientProviderAutoConfiguration {

  @Bean
  public OpenidWebClientProvider openidWebClientProvider(WebClient.Builder webClientBuilder,
      Logbook logbook) {
    return new OpenidWebClientProviderImpl(webClientBuilder, logbook);
  }
}
