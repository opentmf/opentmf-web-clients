package org.opentmf.client.basic.exception;

import org.opentmf.client.common.exception.OpenTmfWebClientException;
import java.io.Serial;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * @author Gokhan Demir
 */
@Getter
public class BasicWebClientException extends OpenTmfWebClientException {

  @Serial
  private static final long serialVersionUID = 3L;

  public BasicWebClientException(HttpStatusCode httpStatus) {
    super(httpStatus);
  }

  public BasicWebClientException(HttpStatusCode httpStatusCode, String message) {
    super(httpStatusCode, message);
  }

  public BasicWebClientException(HttpStatusCode httpStatusCode, String message, Throwable cause) {
    super(httpStatusCode, message, cause);
  }
}
