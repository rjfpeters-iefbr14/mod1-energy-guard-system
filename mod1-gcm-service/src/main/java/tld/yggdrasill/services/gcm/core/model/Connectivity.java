package tld.yggdrasill.services.gcm.core.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
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
        "Defines the connectivity of a contingency, contains underlying measuring points and EANs.")
public class Connectivity {
  @Schema(description = "List of IDs of underlying measuring points")
  private List<Integer> measuringPoints;

  @Schema(description = "List of IDs of underlying EANs")
  private List<Integer> customers;
}
