package tld.yggdrasill.services.gcm.api.model.v1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tld.yggdrasill.services.gcm.core.model.CimType;
import tld.yggdrasill.services.gcm.core.model.ContingencyEquipment;
import tld.yggdrasill.services.gcm.core.model.ContingencyStatus;
import tld.yggdrasill.services.gcm.core.model.Name;
import tld.yggdrasill.services.gcm.core.model.ProductGroup;
import tld.yggdrasill.services.gcm.core.model.TopologicalIsland;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContingencyRequest {

  private static final CimType type = CimType.CONTINGENCY;

  @Schema(description = "Name of the contingency.", example = "Neerijnen")
  @NotNull
  private String name;

  @Schema(description = "Description of the contingency.", example = "Contingency RS Neerijnen")
  private String description;

  @Schema(description = "Alternative name's to describe the contingency.")
  private List<Name> names;

  @Schema(description = "Equipment(s) which form the contingency.")
  private List<ContingencyEquipment> contingencyEquipments;

  @Schema(description = "Network topology description.")
  private TopologicalIsland topologicalIsland;

  @Schema(description = "Lists which product groups apply for this contingency.")
  private List<ProductGroup> productGroups;

  @Schema(
    description = "Expected start datetime (ISO-8601) of the run workflow for the contingency.",
    example = "2021-10-01T00:00:00Z")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  private OffsetDateTime expectedStartDateTime;

  @Schema(
    description = "Expected end datetime (ISO-8601) of the run workflow for the contingency.",
    example = "2021-10-31T00:00:00Z")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  private OffsetDateTime expectedEndDateTime;

  @Schema(description = "Status of the contingency.", required = true)
  @NotNull
  private ContingencyStatus status;

  public CimType getType() {
    return type;
  }
}
