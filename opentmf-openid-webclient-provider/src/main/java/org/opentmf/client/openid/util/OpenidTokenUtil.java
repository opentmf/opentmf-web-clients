package org.opentmf.client.openid.util;

import static org.opentmf.client.common.util.TokenUtil.firstNonNull;

import org.opentmf.client.openid.model.OpenidTokenProperties;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

/**
 * @author Gokhan Demir
 */
@UtilityClass
public class OpenidTokenUtil {

  public static final String SCOPE = "scope";

  public static String findScope(String additionalScopes, String configuredScopes,
      Map<String, String> enricher) {
    var scope = Optional.ofNullable(additionalScopes)
        .map(String::trim)
        .orElse("") + " ";

    scope += Optional.ofNullable(configuredScopes)
        .map(String::trim)
        .orElse("") + " ";

    scope += Optional.ofNullable(enricher.get(SCOPE))
        .map(String::trim)
        .orElse("") + " ";

    return Arrays.stream(scope.trim().split(" "))
        .filter(StringUtils::hasText)
        .collect(Collectors.toCollection(TreeSet::new))
        .stream().map(String::toString).collect(Collectors.joining(" "));
  }

  public static String findUsername(OpenidTokenProperties properties, Map<String, String> enricher) {
    var formData = properties.getFormData();
    return firstNonNull(
        enricher.get(properties.getUsernameField()),
        formData.get(properties.getUsernameField())
    );
  }
}
