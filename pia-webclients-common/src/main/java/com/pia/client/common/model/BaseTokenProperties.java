package com.pia.client.common.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

/**
 * @author Gokhan Demir
 */
@Validated
@Getter
@Setter
public abstract class BaseTokenProperties {

  /**
   * When true, returns a dummy token.
   */
  private boolean useMock = false;
}