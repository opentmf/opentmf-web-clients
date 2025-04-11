package org.opentmf.client.openid.service.api;

import org.opentmf.client.common.service.api.WebClientProvider;
import org.opentmf.client.openid.model.OpenidClientProperties;

/**
 * @author Gokhan Demir
 */
public interface OpenidWebClientProvider extends
    WebClientProvider<OpenidClientProperties, OpenidTokenService> {
}
