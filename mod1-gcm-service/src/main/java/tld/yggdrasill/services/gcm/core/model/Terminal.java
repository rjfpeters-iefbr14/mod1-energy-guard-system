package tld.yggdrasill.services.gcm.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Terminal {

  @Schema(example = "Terminal")
  private static final CimType type = CimType.TERMINAL;

  @JsonProperty(value = "mRID", index = 0)
  private UUID mRID;

  private List<Measurement> measurements;

  @JsonProperty(value = "@type", index = 1)
  public CimType getType() {
    return type;
  }
}
