package com.pia.client.openid.service.impl;

import static com.pia.client.openid.util.OpenidTokenUtil.SCOPE;
import static org.springframework.util.StringUtils.hasText;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pia.client.common.util.WebClientUtil;
import com.pia.client.openid.exception.OpenidWebClientException;
import com.pia.client.openid.model.OpenidClientProperties;
import com.pia.client.openid.service.api.OpenidTokenClient;
import com.pia.commons.util.JacksonUtil;
import java.net.URI;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author Gokhan Demir
 */
@Slf4j
@RequiredArgsConstructor
public class OpenidTokenClientImpl implements OpenidTokenClient {

  private final OpenidClientProperties properties;
  private final WebClient webClient;

  @Override
  public Mono<ObjectNode> retrieveToken(URI tokenUrl, MultiValueMap<String, String> formData) {
    log.debug("Will retrieve a new openid token from url: {}, scope: {}, username: {}",
        tokenUrl, formData.get(SCOPE), formData.get(properties.getTokenConfig().getUsernameField()));
    return post(tokenUrl, BodyInserters.fromFormData(formData));
  }

  private Mono<ObjectNode> post(
      URI url, BodyInserters.FormInserter<String> tokenRequestForm) {
    var basicAuthUsername = properties.getTokenConfig().getBasicAuthUsername();
    var basicAuthPassword = properties.getTokenConfig().getBasicAuthPassword();

    return webClient
        .post()
        .uri(url)
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .accept(MediaType.APPLICATION_JSON)
        .body(tokenRequestForm)
        .headers(headers -> {
          if (hasText(basicAuthUsername) && hasText(basicAuthPassword)) {
            headers.setBasicAuth(basicAuthUsername, basicAuthPassword);
          }
        })
        .retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> WebClientUtil.handleError(clientResponse, OpenidWebClientException.class))
        .bodyToMono(String.class)
        .map(body -> (ObjectNode) JacksonUtil.jsonToTree(body))
        .retryWhen(
            WebClientUtil.retry(properties.getNumRetries(),
                Duration.ofMillis(properties.getRetryWaitMillis())));
  }
}
