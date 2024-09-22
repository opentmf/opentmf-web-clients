package com.pia.client.basic.exception;

import com.pia.client.common.exception.PiaWebClientException;
import java.io.Serial;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * @author Gokhan Demir
 */
@Getter
public class BasicWebClientException extends PiaWebClientException {

  @Serial
  private static final long serialVersionUID = 1L;

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
