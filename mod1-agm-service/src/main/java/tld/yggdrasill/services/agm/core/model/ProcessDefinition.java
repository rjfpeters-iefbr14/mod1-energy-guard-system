package tld.yggdrasill.services.agm.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@ToString
public class ProcessDefinition implements Serializable {

  private String contingencyId;
  private String contingencyName;
  private String description;

  private String specificationFileName;
  private String specificationFileContentType;
  private String bpmnProcessDefinitionId;
  private String camundaDeploymentId;
  private String camundaProcessDefinitionId;

  @Builder.Default private String tenantId = "system.operations";
}
