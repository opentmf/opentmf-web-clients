package org.opentmf.client.openid.config;

import static org.opentmf.client.common.util.TokenUtil.CLIENT_PROPERTIES;
import static org.opentmf.client.common.util.TokenUtil.TOKEN_SERVICE;
import static org.opentmf.client.common.util.TokenUtil.WEB_CLIENT;

import org.opentmf.client.openid.model.OpenidClientProperties;
import org.opentmf.client.openid.service.api.OpenidWebClientProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author Gokhan Demir
 */
@RequiredArgsConstructor
@Slf4j
public class OpenIdBeanRegistrationUtil {

  private final ConfigurableListableBeanFactory factory;
  private final OpenidWebClientProvider openidWebClientProvider;

  void registerBeansIfNecessary(String prefix, OpenidClientProperties properties) {
    if (!factory.containsBean(prefix + CLIENT_PROPERTIES)) {
      factory.registerSingleton(prefix + CLIENT_PROPERTIES, properties);
      logExposed(prefix + CLIENT_PROPERTIES);
    }
    if (!factory.containsBean(prefix + WEB_CLIENT)) {
      factory.registerSingleton(prefix + WEB_CLIENT,
          openidWebClientProvider.buildWebClient(properties));
      logExposed(prefix + WEB_CLIENT);
    }
    if (!factory.containsBean(prefix + TOKEN_SERVICE)) {
      factory.registerSingleton(prefix + TOKEN_SERVICE,
          openidWebClientProvider.buildTokenService(properties));
      logExposed(prefix + TOKEN_SERVICE);
    }
  }

  private void logExposed(String beanName) {
    log.debug("Exposed: {} bean for openId auth.", beanName);
  }
}
