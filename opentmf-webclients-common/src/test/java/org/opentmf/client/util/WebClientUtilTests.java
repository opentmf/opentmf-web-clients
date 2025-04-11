package org.opentmf.client.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.opentmf.client.common.exception.OpenTmfWebClientException;
import org.opentmf.client.common.util.WebClientUtil;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.retry.RetryBackoffSpec;

/**
 * @author Gokhan Demir
 * @author Cezmi Aslan
 */
@ExtendWith(MockitoExtension.class)
class WebClientUtilTests {

  @Mock
  HttpRequest httpRequest;
  @Mock
  ClientResponse clientResponse;

  @ParameterizedTest
  @EnumSource(value = HttpStatus.class, names = {
      "REQUEST_TIMEOUT",
      "TOO_MANY_REQUESTS",
      "INTERNAL_SERVER_ERROR",
      "BAD_GATEWAY",
      "SERVICE_UNAVAILABLE",
      "GATEWAY_TIMEOUT",
      "BANDWIDTH_LIMIT_EXCEEDED"
  })
  void testRetryableCodes_Success(HttpStatus httpStatus) {
    Assertions.assertTrue(WebClientUtil.isRetryableStatus(httpStatus));
  }

  @Test
  void testRetryableCodes_NullValue() {
    Assertions.assertFalse(WebClientUtil.isRetryableStatus(null));
  }

  @ParameterizedTest
  @EnumSource(value = HttpStatus.class, names = {
      "REQUEST_TIMEOUT",
      "TOO_MANY_REQUESTS",
      "INTERNAL_SERVER_ERROR",
      "BAD_GATEWAY",
      "SERVICE_UNAVAILABLE",
      "GATEWAY_TIMEOUT",
      "BANDWIDTH_LIMIT_EXCEEDED"
  })
  void testShouldRetryOn_Success(HttpStatus status) {
    Assertions.assertTrue(WebClientUtil.shouldRetryOn(
        new WebClientResponseException(status.value(), "", null, null, null)));
    Assertions.assertTrue(WebClientUtil.shouldRetryOn(new OpenTmfWebClientException(status)));
  }

  @Test
  void testShouldRetryOn_FailCases() {
    Assertions.assertFalse(WebClientUtil.shouldRetryOn(null));
    Assertions.assertFalse(WebClientUtil.shouldRetryOn(new Exception()));

    OpenTmfWebClientException teapot = new OpenTmfWebClientException(HttpStatus.I_AM_A_TEAPOT);
    OpenTmfWebClientException unauthorized = new OpenTmfWebClientException(HttpStatus.UNAUTHORIZED, "");
    OpenTmfWebClientException versionNotSupported = new OpenTmfWebClientException(
        HttpStatus.HTTP_VERSION_NOT_SUPPORTED, "HTTP version not supported", new Exception());

    Assertions.assertEquals(HttpStatus.I_AM_A_TEAPOT, teapot.getStatusCode());
    Assertions.assertEquals(HttpStatus.I_AM_A_TEAPOT.value(), teapot.getRawStatusCode());
    Assertions.assertEquals(HttpStatus.UNAUTHORIZED, unauthorized.getStatusCode());
    Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), unauthorized.getRawStatusCode());
    Assertions.assertEquals(HttpStatus.HTTP_VERSION_NOT_SUPPORTED,
        versionNotSupported.getStatusCode());
    Assertions.assertEquals(HttpStatus.HTTP_VERSION_NOT_SUPPORTED.value(),
        versionNotSupported.getRawStatusCode());

    Assertions.assertFalse(WebClientUtil.shouldRetryOn(teapot));
    Assertions.assertFalse(WebClientUtil.shouldRetryOn(unauthorized));
    Assertions.assertFalse(WebClientUtil.shouldRetryOn(versionNotSupported));
  }

  @Test
  void testRetry_WithoutJitter() {
    RetryBackoffSpec spec = WebClientUtil.retry(1, Duration.ofSeconds(1));
    Assertions.assertEquals(0.0d, spec.jitterFactor);
  }

  @Test
  void testHandleError_withEmptyResponse_ThrowsOpenTmfWebClientException() {
    when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);
    when(httpRequest.getURI()).thenReturn(URI.create("http://localhost:8080"));
    when(clientResponse.request()).thenReturn(httpRequest);
    when(clientResponse.statusCode()).thenReturn(HttpStatus.BAD_REQUEST);
    when(clientResponse.bodyToMono(any(Class.class))).thenReturn(Mono.empty());

    StepVerifier.create(WebClientUtil.handleError(clientResponse, OpenTmfWebClientException.class))
        .expectError(OpenTmfWebClientException.class)
        .verify();
  }

  @Test
  void testHandleError_withNotResponse_returnsErrorMessage() {
    when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);
    when(httpRequest.getURI()).thenReturn(URI.create("http://localhost:8080"));
    when(clientResponse.request()).thenReturn(httpRequest);
    when(clientResponse.statusCode()).thenReturn(HttpStatus.BAD_REQUEST);
    when(clientResponse.bodyToMono(any(Class.class)))
        .thenReturn(Mono.just("{\"message\":\"Not Found\"}"));
    StepVerifier.create(WebClientUtil.handleError(clientResponse, OpenTmfWebClientException.class))
        .assertNext(Assertions::assertNotNull)
        .verifyComplete();
  }

  @Test
  void fluxRetryBackoff_withNotRetryableError_DoesNotRetry() {
    List<Long> elapsedList = new ArrayList<>();
    StepVerifier
        .withVirtualTime(() -> Flux
            .concat(Flux.error(new IllegalArgumentException("myException")),
                Flux.error(new IllegalArgumentException("myException")))
            .retryWhen(WebClientUtil.retry(3, Duration.ofSeconds(1)))
            .onErrorResume(throwable -> Mono.just(0)).elapsed().filter(t -> t.getT1() > 0)
            .doOnNext(elapsed -> elapsedList.add(elapsed.getT1())).map(Tuple2::getT2))
        .expectSubscription().thenAwait(Duration.ofSeconds(7)).verifyComplete();
    assertTrue(elapsedList.isEmpty());
  }

  @Test
  void fluxRetryBackoff_withRetryableError_DoesRetry() {
    List<Long> elapsedList = new ArrayList<>();
    StepVerifier
        .withVirtualTime(() -> Flux
            .concat(Flux.error(new OpenTmfWebClientException(HttpStatus.BAD_GATEWAY)),
                Flux.error(new OpenTmfWebClientException(HttpStatus.BAD_GATEWAY)))
            .retryWhen(WebClientUtil.retry(3, Duration.ofSeconds(1)))
            .onErrorResume(throwable -> Mono.just(0)).elapsed().filter(t -> t.getT1() > 0)
            .doOnNext(elapsed -> elapsedList.add(elapsed.getT1())).map(Tuple2::getT2))
        .expectSubscription().thenAwait(Duration.ofSeconds(7))
        .assertNext(Assertions::assertNotNull).verifyComplete();
    assertFalse(elapsedList.isEmpty());
  }

  @Test
  void fluxRetryBackoff_withRetryableError_GetError() {
    var retryBuilder = WebClientUtil.retry(3, Duration.ofSeconds(1));

    StepVerifier
        .create(
            Flux.error(new OpenTmfWebClientException(HttpStatus.BAD_GATEWAY)).retryWhen(retryBuilder))
        .thenAwait(Duration.ofSeconds(2))
        .expectErrorSatisfies(e -> assertThat(e).isInstanceOf(OpenTmfWebClientException.class))
        .verify();
  }
}
