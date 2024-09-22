package com.pia.client.basic.service.api;

import com.pia.client.basic.model.BasicClientProperties;
import com.pia.client.common.service.api.WebClientProvider;

/**
 * @author Gokhan Demir
 */
public interface BasicWebClientProvider extends
    WebClientProvider<BasicClientProperties, BasicTokenService> {
}
