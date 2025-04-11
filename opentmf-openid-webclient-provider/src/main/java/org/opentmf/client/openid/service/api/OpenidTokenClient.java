package org.opentmf.client.openid.service.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

/**
 * @author Gokhan Demir
 */
public interface OpenidTokenClient {

  /**
   * Retrieves an access token from the tokenUrl.
   *
   * @param tokenUrl the URL to use to obtain an access token.
   * @param formData the configured and potentially enriched form data
   * @return the retrieved JSON object that has the accessToken.
   */
  Mono<ObjectNode> retrieveToken(URI tokenUrl, MultiValueMap<String, String> formData);
}
