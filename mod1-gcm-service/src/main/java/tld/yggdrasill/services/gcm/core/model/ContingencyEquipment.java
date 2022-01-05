package tld.yggdrasill.services.gcm.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContingencyEquipment {

  @Schema(example = "ContingencyEquipment")
  private static final CimType type = CimType.CONTINGENCY_EQUIPMENT;

  @JsonProperty(value = "mRID", index = 0)
  @Schema(description = "mRID of the equipment.")
  private UUID mRID;

  @Schema(description = "Name of the equipment.")
  private String name;

  @Schema(description = "Description of the equipment.")
  private String description;

  @Schema(description = "Alternative names/identifiers for this equipment.")
  private List<Name> names;

  @Schema(description = "Terminals connected to this equipment.")
  private List<Terminal> terminals;

  @Schema(description = "Limits associated with this equipment.")
  private List<Limit> limitSet;

  @JsonProperty(value = "@type", index = 1)
  public CimType getType() {
    return type;
  }
}
