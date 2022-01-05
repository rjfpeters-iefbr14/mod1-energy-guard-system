package tld.yggdrasill.services.gcm.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Limit {

  @Schema(example = "comfortLimitODN")
  private String name;

  @Schema(example = "Comfort limit ODN")
  private String description;

  @Schema(description = "Active power")
  private ActivePower value;
}
