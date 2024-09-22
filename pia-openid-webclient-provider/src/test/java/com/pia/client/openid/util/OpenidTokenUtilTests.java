package com.pia.client.openid.util;

import static com.pia.client.openid.util.OpenidTokenUtil.SCOPE;
import static com.pia.client.openid.util.OpenidTokenUtil.USERNAME;
import static com.pia.client.openid.util.OpenidTokenUtil.findScope;
import static com.pia.client.openid.util.OpenidTokenUtil.findUsername;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * @author Gokhan Demir
 */
class OpenidTokenUtilTests {

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
    assertEquals("b", findUsername(Map.of(USERNAME, "a"), Map.of(USERNAME, "b")));
    assertEquals("a", findUsername(Map.of(USERNAME, "a"), Map.of()));
    assertEquals("b", findUsername(Map.of(), Map.of(USERNAME, "b")));
    assertNull(findUsername(Map.of(), Map.of()));
  }
}
