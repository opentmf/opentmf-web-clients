package com.pia.client.util;

import static com.pia.client.common.util.TokenUtil.cacheKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pia.client.common.util.TokenUtil;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

/**
*
* @author Cezmi Aslan
*/
class TokenUtilTests {

  @Test
  void testCacheProviderExists() {
    assertTrue(StringUtils.hasText(TokenUtil.CACHING_PROVIDER));
  }

  @Test
  void test_cacheKey_withValidParameters_returnsValidResult() {
    assertEquals("user scope https://localhost:443",
        cacheKey(URI.create("https://localhost:443"), "scope", "user"));
  }
}
