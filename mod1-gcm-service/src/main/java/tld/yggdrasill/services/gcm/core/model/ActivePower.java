package tld.yggdrasill.services.gcm.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivePower {

  @Schema(example = "23.0")
  private Double value;

  @Schema(example = "M")
  private UnitMultiplier multiplier;

  @Schema(example = "W")
  private UnitSymbol unit;
}
