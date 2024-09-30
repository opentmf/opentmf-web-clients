package com.pia.client.common.util;

import com.pia.client.common.exception.PiaWebClientException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

/**
 * Common static utility methods for use with the WebClient calls.
 *
 * @author Gokhan Demir
 */
@Slf4j
public final class WebClientUtil {

  private static final double DEFAULT_JITTER_FACTOR = 0.0d;

  private WebClientUtil() {
  }

  /**
   * This error handling method is provided as a template. Use your own error handling instead of
   * this one.
   *
   * @param clientResponse The received client response from the server.
   * @return the handled exception wrapped inside a mono.
   */
  public static Mono<Throwable> handleError(ClientResponse clientResponse,
      Class<? extends PiaWebClientException> exceptionClass) {
    var request = clientResponse.request();
    var httpStatus = clientResponse.statusCode();
    log.debug("Handling {} for {} {}", httpStatus, request.getMethod(), request.getURI());
    return clientResponse
        .bodyToMono(String.class)
        .switchIfEmpty(Mono.defer(() -> Mono.error(createException(httpStatus, exceptionClass))))
        .map(message -> createException(httpStatus, message, exceptionClass));
  }

  private static PiaWebClientException createException(
      HttpStatusCode httpStatusCode, Class<? extends PiaWebClientException> exception) {
    try {
      return exception.getDeclaredConstructor(HttpStatusCode.class).newInstance(httpStatusCode);
    } catch (Exception e) {
      throw new IllegalArgumentException("Exception class misses required constructor.", e);
    }
  }

  private static PiaWebClientException createException(
      HttpStatusCode httpStatusCode, String message, Class<? extends PiaWebClientException> exception) {
    try {
      return exception
          .getDeclaredConstructor(HttpStatusCode.class, String.class)
          .newInstance(httpStatusCode, message);
    } catch (Exception e) {
      throw new IllegalArgumentException("Exception class misses required constructor.", e);
    }
  }

  /**
   * The list of HTTP status codes that are worth retrying the original call (i.e. without making
   * any changes to the original call).
   */
  private static final Set<HttpStatus> RETRYABLE_STATUS_CODES = new HashSet<>(
      Arrays.asList(
          HttpStatus.REQUEST_TIMEOUT,
          HttpStatus.TOO_MANY_REQUESTS,
          HttpStatus.INTERNAL_SERVER_ERROR,
          HttpStatus.BAD_GATEWAY,
          HttpStatus.SERVICE_UNAVAILABLE,
          HttpStatus.GATEWAY_TIMEOUT,
          HttpStatus.BANDWIDTH_LIMIT_EXCEEDED
      )
  );

  /**
   * Returns true if this httpStatus is worth trying. Worth trying means, there would be chance to
   * get a success code after retrying.
   *
   * @param httpStatusCode The httpStatus if available. False will be returned on null status code.
   * @return true if this httpStatus is worth trying, false otherwise.
   */
  public static boolean isRetryableStatus(HttpStatusCode httpStatusCode) {
    return httpStatusCode != null
        && RETRYABLE_STATUS_CODES.contains(HttpStatus.resolve(httpStatusCode.value()));
  }

  /**
   * A generic method that decides whether retry a web call or not depending on the exception
   * caught. It would be meaningful to use this method if the underlying client has extended their
   * managed exceptions from SolutionHubWebClientException.
   *
   * @param throwable the caught and preferably managed exception during a web client call.
   * @return true if the exception contains a retryable HTTP status code, false otherwise.
   */
  public static boolean shouldRetryOn(Throwable throwable) {
    HttpStatusCode status = null;
    if (throwable instanceof PiaWebClientException piaWebClientException) {
      status = piaWebClientException.getStatusCode();
    } else if (throwable instanceof WebClientResponseException webClientResponseException) {
      status = webClientResponseException.getStatusCode();
    }
    return WebClientUtil.isRetryableStatus(status);
  }

    /**
     * Will generate a RetryBackoffSpec with the specified jitter factor. The users of this method
     * should include their own onRetryExhaustedThrow implementation, right after this retry method.
     *
     * @param maxAttempts Max attempts.
     * @param duration    Duration.
     * @return The generated RetryBackoffSpec.
     */
  public static RetryBackoffSpec retry(long maxAttempts, Duration duration, double jitterFactor) {
    return Retry.backoff(maxAttempts, duration)
        .jitter(jitterFactor)
        .doAfterRetry(retrySignal -> log.warn("Will retry. [Retry count: {}][Retry LocalTime: {}]",
            retrySignal.totalRetries(), LocalTime.now()))
        .filter(WebClientUtil::shouldRetryOn)
        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure());
  }

  /**
   * Will generate a RetryBackoffSpec with a jitter factor of zero (i.e. without a jitter factor).
   * For exhausted retries, this method will not return a RetryExhaustedException, and instead it
   * will return the latest error received from the latest retry.
   *
   * @param maxAttempts Max attempts.
   * @param duration    Duration.
   * @return The generated RetryBackoffSpec.
   * @see WebClientUtil#retry(long, Duration, double)
   */
  public static RetryBackoffSpec retry(long maxAttempts, Duration duration) {
    return retry(maxAttempts, duration, DEFAULT_JITTER_FACTOR);
  }
}
