package tld.yggdrasill.services.agm.core.flow;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tld.yggdrasill.services.agm.client.GridServiceEventBuilder;
import tld.yggdrasill.services.agm.client.GridServiceProducerService;
import tld.yggdrasill.services.agm.client.contingency.ContingencyClient;
import tld.yggdrasill.services.agm.client.contingency.model.Contingency;
import tld.yggdrasill.services.cgs.model.GridServiceEvent;

import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
public class PublishSafetyAssessmentAdapter implements JavaDelegate {

  public static final String ERROR_SAFETY_ASSESSMENT = "Error_SafetyAssessment";

  private final ContingencyClient contingencyClient;

  private final GridServiceProducerService kafkaProducer;

  @Value("${info.app.name}")
  private String producerId;

  public PublishSafetyAssessmentAdapter(
    ContingencyClient contingencyClient,
    GridServiceProducerService kafkaProducer) {
    this.contingencyClient = contingencyClient;
    this.kafkaProducer = kafkaProducer;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    String processId = execution.getProcessInstanceId();
    String contingencyId = (String) execution.getVariable("contingencyId");
    String contingencyName = (String) execution.getVariable("contingencyName");

    try {
      log.info("ProcessInstance: {}, {}", kv("processId", processId), kv("contingencyId", contingencyId));

      Contingency contingency = contingencyClient.getContingencyById(contingencyId);
      log.info("Contingency: {} -> {}", kv("contingencyId", contingencyId), contingency.toString());

      String businessKey = UUID.randomUUID().toString();
      execution.setProcessBusinessKey(businessKey);
      log.info("Starting Contingency Guard: {}, {}, {}", kv("contingencyId",
        contingencyId), kv("contingencyName", contingencyName), kv("businessKey", businessKey));

      GridServiceEvent event = GridServiceEventBuilder
        .buildEvent(producerId, contingencyId, contingencyName, businessKey);
      kafkaProducer.send(event);
    } catch (Exception e) {
      log.error("ERROR_SAFETY_ASSESSMENT {} {}",kv("contingencyName", contingencyName),e.getMessage());
      execution.setVariable("errorCode {}",kv("contingencyId", contingencyId));
      throw new BpmnError(ERROR_SAFETY_ASSESSMENT);
    }
  }
}

