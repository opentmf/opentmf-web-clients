package com.pia.client.basic.model;

import com.pia.client.common.model.BaseClientProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

/**
 * @author Gokhan Demir
 */
@Getter
@Setter
@Validated
public class BasicClientProperties extends BaseClientProperties<BasicTokenProperties> {
}
