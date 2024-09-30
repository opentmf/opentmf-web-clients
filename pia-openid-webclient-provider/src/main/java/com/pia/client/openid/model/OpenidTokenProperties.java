package com.pia.client.openid.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for getting an openId auth token.
 *
 * @author Gokhan Demir
 */
@Validated
@Getter
@Setter
public class OpenidTokenProperties {

  /**
   * When true, returns a dummy token.
   */
  private boolean useMock = false;

  /**
   * The URL to use to obtain a token
   */
  @NotNull
  private URI tokenUrl;

  /**
   * The username for the basic auth portion of the token post request, if the token issuer requires
   * additional basic authentication as well.
   */
  private String basicAuthUsername;

  /**
   * The password for the basic auth portion of the token post request, if the token issuer requires
   * additional basic authentication as well.
   */
  private String basicAuthPassword;

  /**
   * The name of the access token cache. Must be unique within an application scope.
   */
  @NotEmpty
  private String cacheName;

  /**
   * The expiry duration of the records in the accessToken cache.
   */
  @NotNull
  private long cacheExpirySeconds;

  /**
   * The field that holds the real access_token. Defaults to "access_token".
   */
  @NotEmpty
  private String tokenField = "access_token";

  /**
   * Necessary form data to obtain a token
   */
  @NotEmpty
  private Map<@NotBlank String, @NotBlank String> formData;
}
