package org.opentmf.client.basic.service.api;

import org.opentmf.client.basic.model.BasicClientProperties;
import org.opentmf.client.common.service.api.WebClientProvider;

/**
 * @author Gokhan Demir
 */
public interface BasicWebClientProvider extends
    WebClientProvider<BasicClientProperties, BasicTokenService> {
}
