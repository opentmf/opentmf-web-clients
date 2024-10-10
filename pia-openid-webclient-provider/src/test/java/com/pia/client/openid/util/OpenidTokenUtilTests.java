package com.pia.client.openid.util;

import static com.pia.client.openid.util.OpenidTokenUtil.SCOPE;
import static com.pia.client.openid.util.OpenidTokenUtil.findScope;
import static com.pia.client.openid.util.OpenidTokenUtil.findUsername;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.util.StringUtils.hasText;

import com.pia.client.openid.model.OpenidTokenProperties;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * @author Gokhan Demir
 */
class OpenidTokenUtilTests {

  public static final String USERNAME = "username";

  @Test
  void testFindScope_withAllValues_returnsExpectedResult() {
    assertEquals("a b c d e", findScope("a b", "a b c", Map.of(SCOPE, "c d e")));
    assertEquals("a b c d e", findScope("b a", "c a b", Map.of(SCOPE, "e d c")));
    assertEquals("a b c d e", findScope(null, "c a b", Map.of(SCOPE, "e d c")));
    assertEquals("a b c d e", findScope("b a", null, Map.of(SCOPE, "e d c")));
    assertEquals("c d e", findScope(null, null, Map.of(SCOPE, "e d c")));
    assertEquals("", findScope(null, null, Map.of()));
  }

  @Test
  void testFindUsername_withAllValues_returnsExpectedResult() {
    assertEquals("b", findUsername(props("a"), map("b")));
    assertEquals("a", findUsername(props("a"), Map.of()));
    assertEquals("b", findUsername(props(""), map("b")));
    assertNull(findUsername(props(""), Map.of()));
  }

  OpenidTokenProperties props(String s) {
    OpenidTokenProperties properties = new OpenidTokenProperties();
    properties.setUsernameField(USERNAME);
    if (hasText(s)) {
      properties.setFormData(map(s));
    } else {
      properties.setFormData(Collections.emptyMap());
    }
    return properties;
  }

  Map<String, String> map(String s) {
    return Map.of(USERNAME, s);
  }
}
