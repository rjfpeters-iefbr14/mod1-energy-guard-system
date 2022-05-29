package tld.yggdrasill.services.gcm.api.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tld.yggdrasill.services.gcm.core.model.CimType;
import tld.yggdrasill.services.gcm.core.model.Terminal;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopologicalIslandResponse {

  @Schema(example = "TopologicalIsland")
  private static CimType type = CimType.TOPOLOGICAL_ISLAND;

  private List<Terminal> terminals;

  @JsonProperty(value = "@type", index = 0)
  public CimType getType() {
    return type;
  }
}
