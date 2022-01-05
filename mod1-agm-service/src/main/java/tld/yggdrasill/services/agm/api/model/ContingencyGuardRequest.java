package tld.yggdrasill.services.agm.api.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class ContingencyGuardRequest implements Serializable {

  @NotNull
  private String contingencyId;

  @NotNull
  private String contingencyName;

}
