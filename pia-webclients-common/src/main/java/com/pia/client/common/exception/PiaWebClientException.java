package com.pia.client.common.exception;

import java.io.Serial;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * This exception should be the base for all explicitly handled exceptions that contain an HTTP
 * status code.
 *
 * @author Gokhan Demir
 */
@Getter
public class PiaWebClientException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 2L;

  private final HttpStatusCode statusCode;

  public PiaWebClientException(HttpStatusCode httpStatus) {
    this.statusCode = httpStatus;
  }

  public PiaWebClientException(HttpStatusCode httpStatusCode, String message) {
    super(message);
    this.statusCode = httpStatusCode;
  }

  public PiaWebClientException(HttpStatusCode httpStatusCode, String message, Throwable cause) {
    super(message, cause);
    this.statusCode = httpStatusCode;
  }

  public final int getRawStatusCode() {
    return statusCode.value();
  }

}
