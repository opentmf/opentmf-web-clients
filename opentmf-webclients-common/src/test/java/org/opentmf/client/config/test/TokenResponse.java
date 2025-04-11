package org.opentmf.client.config.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Cezmi Aslan
 */
@Getter
@Setter
@JsonNaming(SnakeCaseStrategy.class)
public class TokenResponse implements Serializable {

  @Serial
  private static final long serialVersionUID = 2L;

  private String accessToken;
  private String tokenType;
  private String expiresIn;
  private String issuedAt;
  private String scope;

  @JsonIgnore
  public String calculateExpiresIn() {
    try {
      var t0 = System.currentTimeMillis();
      var t1 = (Long.parseLong(issuedAt) * 1000L) + (Long.parseLong(expiresIn) * 1000L);
      return ((t1 - t0) / 1000L) + " seconds";
    } catch (NumberFormatException ignore) {
      return "N/A";
    }
  }

  @JsonIgnore
  public OffsetDateTime calculateExpiresAt() {
    var epocSeconds = (Long.parseLong(issuedAt) + Long.parseLong(expiresIn));
    return Instant.ofEpochSecond(epocSeconds).atOffset(ZoneOffset.UTC);
  }
}
