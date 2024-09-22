package com.pia.client.openid.service.api;

import com.pia.client.common.service.api.WebClientProvider;
import com.pia.client.openid.model.OpenidClientProperties;

/**
 * @author Gokhan Demir
 */
public interface OpenidWebClientProvider extends
    WebClientProvider<OpenidClientProperties, OpenidTokenService> {
}
