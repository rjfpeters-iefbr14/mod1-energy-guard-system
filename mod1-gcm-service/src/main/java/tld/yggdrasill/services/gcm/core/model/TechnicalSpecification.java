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
@Schema(
    description =
        "Technische specifications for this contingency "
            + "\n - electrotechnicalLimit  - Electrotechnical limit of the component as indicated by the producer"
            + "\n - comfortLimitLDN - Comfort limit on LDN side"
            + "\n - comfortLimitODN - Comfort limit on ODN side"
            + "\n - congestionLimitLDN - Congestion limit on LDN side"
            + "\n - congestionLimitODN - Congestion limit on ODN side")
public class TechnicalSpecification {

  // @Schema(hidden = true)
  private AnalogValueLimit electrotechnicalLimit;

  // @Schema(hidden = true)
  private AnalogValueLimit comfortLimitLDN;

  // @Schema(hidden = true)
  private AnalogValueLimit congestionLimitLDN;

  // @Schema(hidden = true)
  private AnalogValueLimit comfortLimitODN;

  // @Schema(hidden = true)
  private AnalogValueLimit congestionLimitODN;
}
