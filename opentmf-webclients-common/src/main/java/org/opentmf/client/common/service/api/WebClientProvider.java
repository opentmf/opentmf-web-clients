package org.opentmf.client.common.service.api;

import org.opentmf.client.common.model.BaseClientProperties;
import org.springframework.web.reactive.function.client.WebClient;

public interface WebClientProvider<P extends BaseClientProperties, T extends TokenService> {

  WebClient buildWebClient(P properties);

  T buildTokenService(P properties);
}
