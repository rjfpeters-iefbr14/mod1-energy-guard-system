package tld.yggdrasill.services.agm.core.flow;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tld.yggdrasill.services.agm.client.GridServiceEventBuilder;
import tld.yggdrasill.services.agm.client.GridServiceProducerClient;
import tld.yggdrasill.services.agm.client.contingency.ContingencyClient;
import tld.yggdrasill.services.agm.client.contingency.model.ContingencyResponse;
import tld.yggdrasill.services.cgs.model.GridServiceEvent;

import javax.inject.Named;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
@Named("publishAgreementVerifier")
public class PublishAgreementVerifierAdapter implements JavaDelegate {

  public static final String ERROR_SAFETY_ASSESSMENT = "Error_AgreementVerifier";

  private final ContingencyClient contingencyClient;

  private final GridServiceProducerClient kafkaProducer;

  @Value("${info.app.name}")
  private String producerId;

  public PublishAgreementVerifierAdapter(
    ContingencyClient contingencyClient,
    GridServiceProducerClient kafkaProducer) {
    this.contingencyClient = contingencyClient;
    this.kafkaProducer = kafkaProducer;
  }

  @Override
  public void execute(DelegateExecution execution) {
    String processId = execution.getProcessInstanceId();
    String contingencyId = (String) execution.getVariable("contingencyId");
    String contingencyName = (String) execution.getVariable("contingencyName");

    try {
      log.info("ProcessInstance: {}, {}", kv("processId", processId), kv("contingencyId", contingencyId));

      ContingencyResponse contingency = contingencyClient.getContingencyById(contingencyId);
      log.info("Contingency: {} -> {}", kv("contingencyId", contingencyId), contingency.toString());

      String businessKey = UUID.randomUUID().toString();
      execution.setProcessBusinessKey(businessKey);
      log.info("Starting Contingency Verifier: {}, {}, {}", kv("contingencyId",
        contingencyId), kv("contingencyName", contingencyName), kv("businessKey", businessKey));

      GridServiceEvent event = GridServiceEventBuilder
        .buildEvent(producerId, contingencyId, contingencyName, businessKey);
      event.getPayload().setState("contingency-verifier");
      kafkaProducer.send(event);
    } catch (Exception e) {
      log.error("ERROR_SAFETY_ASSESSMENT {} {}",kv("contingencyName", contingencyName),e.getMessage());
      execution.setVariable("errorCode {}",kv("contingencyId", contingencyId));
      throw new BpmnError(ERROR_SAFETY_ASSESSMENT);
    }
  }
}

