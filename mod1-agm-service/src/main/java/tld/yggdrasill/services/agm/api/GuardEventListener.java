package tld.yggdrasill.services.agm.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import tld.yggdrasill.services.agm.core.service.ProcessGuardService;
import tld.yggdrasill.services.cgs.model.Contingency;
import tld.yggdrasill.services.cgs.model.GridServiceEvent;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
public class GuardEventListener {

  private final ProcessGuardService safetyGuardService;

  private final ProcessEngine camunda;

  @Autowired
  public GuardEventListener(ProcessGuardService safetyGuardService,
    ProcessEngine camunda) {
    this.safetyGuardService = safetyGuardService;
    this.camunda = camunda;
  }

  @KafkaListener(topics = "${app.kafka.consumer.topic}")
  public void processMessage(String message,
    @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
    @Header(KafkaHeaders.RECEIVED_TOPIC) List<String> topics,
    @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

    log.info(
      String.format("Received: %s-%d[%d] \"%s\"\n", topics.get(0), partitions.get(0), offsets.get(0), message)
    );

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      GridServiceEvent event = objectMapper.readValue(message, GridServiceEvent.class);

      String caseId = event.getPayload().getmRID().toString();

      log.info("Message: {}, {}",
        kv("messageId",event.getMessageId()),
        kv("caseId",caseId)
      );
      Contingency contingency = event.getPayload().getContingency();
      log.info("Contingency: {}, {}", kv("contingencyId",
        contingency.getmRID()), kv("contingencyName", contingency.getName()));

      if (!processInstanceExist(caseId)) {
        log.error("Can't find process instance with {}",kv("caseId",caseId));
        return;
      }

      safetyGuardService.processEvent(caseId, event);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
    }
  }

  private boolean processInstanceExist(String businessKey) {
    long correlatingInstances =
      camunda.getRuntimeService()
        .createProcessInstanceQuery()
        .processInstanceBusinessKey(businessKey)
        .count();

    return correlatingInstances == 1;
  }
}
