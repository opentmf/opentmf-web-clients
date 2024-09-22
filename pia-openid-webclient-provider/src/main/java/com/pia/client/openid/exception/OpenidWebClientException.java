package com.pia.client.openid.exception;

import com.pia.client.common.exception.PiaWebClientException;
import java.io.Serial;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * @author Gokhan Demir
 */
@Getter
public class OpenidWebClientException extends PiaWebClientException {

  @Serial
  private static final long serialVersionUID = 1L;

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
