package org.opentmf.client.basic.config;

import static org.opentmf.client.common.util.TokenUtil.CLIENT_PROPERTIES;
import static org.opentmf.client.common.util.TokenUtil.TOKEN_SERVICE;
import static org.opentmf.client.common.util.TokenUtil.WEB_CLIENT;

import org.opentmf.client.basic.model.BasicClientProperties;
import org.opentmf.client.basic.service.api.BasicWebClientProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author Gokhan Demir
 */
@RequiredArgsConstructor
@Slf4j
public class BasicBeanRegistrationUtil {

  private final ConfigurableListableBeanFactory factory;
  private final BasicWebClientProvider basicWebClientProvider;

  void registerBeansIfNecessary(String prefix, BasicClientProperties properties) {
    if (!factory.containsBean(prefix + CLIENT_PROPERTIES)) {
      factory.registerSingleton(prefix + CLIENT_PROPERTIES, properties);
      logExposed(prefix + CLIENT_PROPERTIES);
    }
    if (!factory.containsBean(prefix + WEB_CLIENT)) {
      factory.registerSingleton(prefix + WEB_CLIENT,
          basicWebClientProvider.buildWebClient(properties));
      logExposed(prefix + WEB_CLIENT);
    }
    if (!factory.containsBean(prefix + TOKEN_SERVICE)) {
      factory.registerSingleton(prefix + TOKEN_SERVICE,
          basicWebClientProvider.buildTokenService(properties));
      logExposed(prefix + TOKEN_SERVICE);
    }
  }

  private void logExposed(String beanName) {
    log.debug("Exposed: {} bean for basic auth.", beanName);
  }
}
