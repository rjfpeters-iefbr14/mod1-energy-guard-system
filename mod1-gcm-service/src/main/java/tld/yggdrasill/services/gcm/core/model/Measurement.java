package tld.yggdrasill.services.gcm.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Measurement {

  @Schema(example = "Analog")
  private static final CimType type = CimType.ANALOG;

  @Schema(example = "ThreePhaseActivePower")
  private static final MeasurementType measurementType = MeasurementType.THREE_PHASE_ACTIVE_POWER;

  @JsonProperty(value = "mRID", index = 0)
  private UUID mRID;

  @JsonProperty(value = "@type", index = 1)
  public CimType getType() {
    return type;
  }

  @JsonProperty(value = "measurementType", index = 2)
  public MeasurementType getMeasurementType() {
    return measurementType;
  }
}
