package tld.yggdrasill.services.agm.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContingencyGuardResponse implements Serializable {

  private String camundaDeploymentId;
  private String camundaProcessDefinitionId;
}
