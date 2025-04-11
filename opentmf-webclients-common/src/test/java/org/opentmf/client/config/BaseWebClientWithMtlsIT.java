package org.opentmf.client.config;
import static org.opentmf.client.util.CertificateUtil.setupMtlsMockServer;
import static org.opentmf.commons.util.JacksonUtil.contents;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.opentmf.client.common.exception.OpenTmfWebClientException;
import org.opentmf.client.common.util.WebClientUtil;
import org.opentmf.client.config.test.ClientProperties;
import org.opentmf.client.config.test.TokenResponse;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.function.Consumer;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 *
 * @author Cezmi Aslan
 */
@SpringBootTest
@ActiveProfiles("base-mtls")
@Import(ClientAutoConfiguration.class)
class BaseWebClientWithMtlsIT {

  private MockWebServer server;
  private String serverUrl;

  @Autowired
  private WebClient baseWebClient;
  @Autowired
  private ClientProperties clientProperties;

  @BeforeEach
  public void setUp() throws IOException, KeyStoreException, NoSuchAlgorithmException,
      CertificateException, UnrecoverableKeyException {

    this.server = setupMtlsMockServer(clientProperties);
    this.serverUrl = "https://localhost:" + server.getPort() + "/";
  }

  @AfterEach
  void tearDown() throws IOException {
    if (server != null) {
      this.server.shutdown();
    }
  }

  private WebClient getWebClientWithoutSsl() {
    return baseWebClient.mutate().clientConnector(new ReactorClientHttpConnector())
        .baseUrl(serverUrl).build();
  }


  @Test
  void testGetToken_withValidRequestBodyInput_returnsOk() {
    String basicAuth =
        "Basic " + HttpHeaders.encodeBasicAuth("username", "password", Charset.defaultCharset());
    prepareResponse(
        response -> response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody("Token Found!"));

    Mono<String> result = this.baseWebClient.get().uri(serverUrl + "token").cookie("apiKey", "123")
        .header(HttpHeaders.AUTHORIZATION, basicAuth)
        .retrieve().bodyToMono(String.class);

    StepVerifier.create(result).expectNext("Token Found!").expectComplete()
        .verify(Duration.ofSeconds(3));

    expectRequestCount(1);
    expectRequest(request -> {
      assertThat(request.getHeader(HttpHeaders.COOKIE)).isEqualTo("apiKey=123");
      assertThat(request.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo(basicAuth);
      assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
      clientProperties.getFixedHeaders().forEach((k, v) ->
          assertThat(request.getHeader(k)).isEqualTo(v));
      assertThat(request.getPath()).isEqualTo("/token");
    });
  }


  @Test
  void testGetToken_withJsonResponseEntity_shouldReturnOk() {

    String content = contents("data/token_response.json");

    prepareResponse(response -> response
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setBody(content));

    Mono<ResponseEntity<String>> result =
        baseWebClient.get().uri(serverUrl + "auth/token").cookie(HttpHeaders.COOKIE, "1234")
            .accept(MediaType.APPLICATION_JSON).retrieve().toEntity(String.class);

    StepVerifier.create(result).consumeNextWith(entity -> {
      assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(entity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
      assertThat(entity.getBody()).isEqualTo(content);
    }).expectComplete().verify(Duration.ofSeconds(3));

    expectRequestCount(1);
    expectRequest(request -> {
      assertThat(request.getPath()).isEqualTo("/auth/token");
      assertThat(request.getHeader(HttpHeaders.COOKIE)).isEqualTo("Cookie=1234");
      assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    });
  }

  @Test
  void testGetToken_withoutMtlsClientToMtlsServer_returnsWebClientRequestException() {
    var webClient = getWebClientWithoutSsl();
    String content = contents("data/token_response.json");

    prepareResponse(response -> response
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).setBody(content));

    Mono<ResponseEntity<String>> result =
        webClient.get().uri(serverUrl + "auth/token").cookie(HttpHeaders.COOKIE, "1234")
            .accept(MediaType.APPLICATION_JSON).retrieve().toEntity(String.class);

    StepVerifier.create(result).expectErrorSatisfies(throwable -> {
      assertThat(throwable).isInstanceOf(WebClientRequestException.class);
      WebClientRequestException ex = (WebClientRequestException) throwable;
      assertThat(ex.getMethod()).isEqualTo(HttpMethod.GET);
    }).verify();


  }

  @Test
  void testGetToken_withBodilessEntity_returnsOk() {
    prepareResponse(
        response -> response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

    Mono<ResponseEntity<Void>> result = baseWebClient.get().uri(serverUrl + "auth/token")
        .accept(MediaType.APPLICATION_JSON).retrieve().toBodilessEntity();

    StepVerifier.create(result).consumeNextWith(entity -> {
      assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(entity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
      assertThat(entity.getBody()).isNull();
    }).expectComplete().verify(Duration.ofSeconds(3));

    expectRequestCount(1);
    expectRequest(request -> {
      assertThat(request.getPath()).isEqualTo("/auth/token");
      assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    });
  }

  @Test
  void testGetToken_withHttpsPost_returnsOk() {
    prepareResponse(
        response -> response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(contents("data/token_response.json")));

    Mono<TokenResponse> result = baseWebClient.post().uri(serverUrl + "new/token")
        .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
        .bodyValue("")
        .exchangeToMono(t -> t.bodyToMono(TokenResponse.class));

    StepVerifier.create(result)
        .consumeNextWith(res -> assertAll("getAccessToken",
            () -> assertEquals("post_subscription_events", res.getScope()),
            () -> assertEquals("7iVGxe84f1ew6QENpCD3", res.getAccessToken()),
            () -> assertEquals("bearer", res.getTokenType()),
            () -> assertEquals("43199", res.getExpiresIn())))
        .expectComplete().verify(Duration.ofSeconds(3));
    expectRequestCount(1);
    expectRequest(request -> {
      assertThat(request.getPath()).isEqualTo("/new/token");
      assertThat(request.getHeader(HttpHeaders.ACCEPT)).isEqualTo("application/json");
      assertThat(request.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo("application/json");
    });
  }

  @Test
  void test_withInvalidDomain_returnsWebClientRequestException() {

    String url = "http://XXX";

    Mono<Void> result = baseWebClient.get().uri(url).retrieve().bodyToMono(Void.class);

    StepVerifier.create(result).expectErrorSatisfies(throwable -> {
      assertThat(throwable).isInstanceOf(WebClientRequestException.class);
      WebClientRequestException ex = (WebClientRequestException) throwable;
      assertThat(ex.getMethod()).isEqualTo(HttpMethod.GET);
      assertThat(ex.getUri()).isEqualTo(URI.create(url));
    }).verify();
  }

  @Test
  void testRetry_withTooManyRequestsResponse_throwsSolutionHubWebClientException() {

    prepareResponse(response -> response.setResponseCode(HttpStatus.TOO_MANY_REQUESTS.value()));
    prepareResponse(response -> response.setResponseCode(HttpStatus.TOO_MANY_REQUESTS.value()));

    Mono<ResponseEntity<Void>> result = baseWebClient.get().uri(serverUrl + "test")
        .header(HttpHeaders.AUTHORIZATION, "for_logging_purposes")
        .header(HttpHeaders.COOKIE, "cookie").header(HttpHeaders.SET_COOKIE, "x=y")
        .header(HttpHeaders.SET_COOKIE2, "foo=bar").retrieve()
        .onStatus(HttpStatusCode::isError, clientResponse -> WebClientUtil.handleError(clientResponse, OpenTmfWebClientException.class))
        .toBodilessEntity()
        .retryWhen(WebClientUtil.retry(1, Duration.ofMillis(100L)));

    StepVerifier.create(result).expectErrorMatches(throwable -> {
      assertInstanceOf(OpenTmfWebClientException.class, throwable);
      var exception = (OpenTmfWebClientException) throwable;
      assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getStatusCode());
      return true;
    }).verify(Duration.ofSeconds(2));

    expectRequestCount(2);
    expectRequest(request -> assertThat(request.getPath()).isEqualTo("/test"));
  }

  private void prepareResponse(Consumer<MockResponse> consumer) {
    MockResponse response = new MockResponse();
    consumer.accept(response);
    this.server.enqueue(response);
  }

  private void expectRequest(Consumer<RecordedRequest> consumer) {
    try {
      consumer.accept(this.server.takeRequest());
    } catch (InterruptedException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private void expectRequestCount(int count) {
    assertThat(this.server.getRequestCount()).isEqualTo(count);
  }
}
