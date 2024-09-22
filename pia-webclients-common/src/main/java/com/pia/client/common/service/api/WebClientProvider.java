package com.pia.client.common.service.api;

import com.pia.client.common.model.BaseClientProperties;
import org.springframework.web.reactive.function.client.WebClient;

public interface WebClientProvider<P extends BaseClientProperties<?>, T extends TokenService> {

  WebClient buildWebClient(P properties);

  T buildTokenService(P properties);
}
