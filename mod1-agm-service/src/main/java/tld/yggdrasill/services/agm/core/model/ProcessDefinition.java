package tld.yggdrasill.services.agm.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDefinition {

  private String contingencyId;
  private String contingencyName;
  private String description;

  private String specificationFileName;
  private String bpmnProcessDefinitionId;
  private String camundaDeploymentId;
  private String camundaProcessDefinitionId;

  @Value("${app.camunda.tenant}")
  private String tenantId;

  private String timerCycle;;
  private String productGroup;
}
