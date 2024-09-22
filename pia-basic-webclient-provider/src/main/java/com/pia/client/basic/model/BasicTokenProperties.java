package com.pia.client.basic.model;

import com.pia.client.common.model.BaseTokenProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for generating a basic authorization header.
 *
 * @author Gokhan Demir
 */
@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasicTokenProperties extends BaseTokenProperties {

  /**
   * The mandatory basic auth username.
   */
  @NotEmpty
  private String username;

  /**
   * The mandatory basic auth password.
   */
  @NotEmpty
  private String password;

  /**
   * The character set to use to encode username and password. Defaults to
   * US-ASCII if not specified (according to RFC 7617).
   * @see <a href="https://www.rfc-editor.org/rfc/rfc7617">RFC-7617</a>
   */
  private String charset = "US-ASCII";
}
