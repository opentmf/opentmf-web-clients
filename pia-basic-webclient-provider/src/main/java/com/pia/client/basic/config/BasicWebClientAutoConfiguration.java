package com.pia.client.basic.config;

import com.pia.client.basic.model.BasicAuthClients;
import com.pia.client.basic.service.api.BasicWebClientProvider;
import com.pia.client.basic.service.impl.BasicWebClientProviderImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.autoconfigure.LogbookAutoConfiguration;

/**
 * @author Gokhan Demir
 */
@AutoConfiguration(after = LogbookAutoConfiguration.class)
@EnableConfigurationProperties(BasicAuthClients.class)
public class BasicWebClientAutoConfiguration {

  @Bean
  public BasicWebClientProvider basicWebClientProvider(WebClient.Builder webClientBuilder,
      Logbook logbook) {
    return new BasicWebClientProviderImpl(webClientBuilder, logbook);
  }
}
