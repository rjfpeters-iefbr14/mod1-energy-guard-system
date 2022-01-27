package tld.yggdrasill.services.agm.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContingencyGuardRequest implements Serializable {

  @NotNull
  private String contingencyId;

  @NotNull
  private String contingencyName;

  @NotNull
  @Builder.Default private String timerCycle = "R/PT3M";

  @NotNull
  private String productGroup;
}
