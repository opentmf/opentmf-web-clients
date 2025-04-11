package org.opentmf.client.openid.exception;

import org.opentmf.client.common.exception.OpenTmfWebClientException;
import java.io.Serial;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * @author Gokhan Demir
 */
@Getter
public class OpenidWebClientException extends OpenTmfWebClientException {

  @Serial
  private static final long serialVersionUID = 3L;

  public OpenidWebClientException(HttpStatusCode httpStatus) {
    super(httpStatus);
  }

  public OpenidWebClientException(HttpStatusCode httpStatusCode, String message) {
    super(httpStatusCode, message);
  }

  public OpenidWebClientException(HttpStatusCode httpStatusCode, String message, Throwable cause) {
    super(httpStatusCode, message, cause);
  }
}
