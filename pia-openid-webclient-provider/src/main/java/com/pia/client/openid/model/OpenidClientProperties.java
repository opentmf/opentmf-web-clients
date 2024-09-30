package com.pia.client.openid.model;

import com.pia.client.common.model.BaseClientProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

/**
 * @author Gokhan Demir
 */
@Getter
@Setter
@Validated
public class OpenidClientProperties extends BaseClientProperties {

  /**
   * A map of arbitrary names for path and scope.
   */
  private Map<@NotEmpty String, @Valid PathScope> paths;

  @Validated
  @Getter
  @Setter
  public static class PathScope {

    /**
     * The client path.
     */
    @NotBlank
    private String path;

    /**
     * The required scope(s) for this client path. If more than one scope is required for this path,
     * then use space character as the scope delimiter.
     */
    private String scope;
  }

  /**
   * OpenID Auth Token Properties.
   */
  private OpenidTokenProperties tokenConfig;
}
