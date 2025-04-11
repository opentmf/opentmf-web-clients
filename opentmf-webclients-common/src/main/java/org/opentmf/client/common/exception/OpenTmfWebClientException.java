package org.opentmf.client.common.exception;

import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * This exception should be the base for all explicitly handled exceptions that contain an HTTP
 * status code.
 *
 * @author Gokhan Demir
 */
@Getter
public class OpenTmfWebClientException extends RuntimeException implements Serializable {

  @Serial
  private static final long serialVersionUID = 3L;

  private final HttpStatusCode statusCode;

  public OpenTmfWebClientException(HttpStatusCode statusCode) {
    this.statusCode = statusCode;
  }

  public OpenTmfWebClientException(HttpStatusCode statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public OpenTmfWebClientException(HttpStatusCode statusCode, String message, Throwable cause) {
    super(message, cause);
    this.statusCode = statusCode;
  }

  public final int getRawStatusCode() {
    return statusCode.value();
  }
}
