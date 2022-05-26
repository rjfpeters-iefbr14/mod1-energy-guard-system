package tld.yggdrasill.services.agm.core.delegate;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import tld.yggdrasill.services.agm.client.safetydossier.SafetyDossierClient;

import javax.inject.Named;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
@Named("createFulFillmentProcessDelegate")
public class CreateFulFillmentProcessDelegate implements JavaDelegate {

  private final SafetyDossierClient safetyDossierClient;

  public CreateFulFillmentProcessDelegate(SafetyDossierClient safetyDossierClient) {
    this.safetyDossierClient = safetyDossierClient;
  }

  public void execute(DelegateExecution execution) throws Exception {
    log.info("... CreateFulFillmentProcessDelegate invoked by "
               + "processDefinitionId=" + execution.getProcessDefinitionId()
               + ", activityId=" + execution.getCurrentActivityId()
               + ", activityName='" + execution.getCurrentActivityName() + "'"
               + ", processInstanceId=" + execution.getProcessInstanceId()
               + ", businessKey=" + execution.getProcessBusinessKey()
               + ", executionId=" + execution.getId());


    //     ContingencyResponse contingency = safetyDossierClient.getSafetyDossierById(contingencyId);
    //     log.info("SafetyDossiers: {} -> {}", kv("contingencyId", contingencyId), contingency.toString());
  }
}
