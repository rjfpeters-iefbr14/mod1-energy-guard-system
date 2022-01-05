package tld.yggdrasill.services.dsa.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import tld.yggdrasill.services.cgs.model.Contingency;
import tld.yggdrasill.services.cgs.model.GridServiceEvent;
import tld.yggdrasill.services.dsa.core.service.DynamicSafetyAnalyzer;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
public class GuardEventListener {

  private final DynamicSafetyAnalyzer dynamicSafetyAnalyzer;

  public GuardEventListener(DynamicSafetyAnalyzer dynamicSafetyAnalyzer) {
    this.dynamicSafetyAnalyzer = dynamicSafetyAnalyzer;
  }

  @KafkaListener(topics = "${app.kafka.consumer.topic}")
  public void processMessage(String message,
    @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
    @Header(KafkaHeaders.RECEIVED_TOPIC) List<String> topics,
    @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

    log.info(
      String.format("Received: %s-%d[%d] \"%s\"\n", topics.get(0), partitions.get(0), offsets.get(0), message)
    );

    //create ObjectMapper instance
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      GridServiceEvent event = objectMapper.readValue(message, GridServiceEvent.class);

      log.info("Message: {}, {}",
        kv("messageId", event.getMessageId()),
        kv("caseId", event.getPayload().getmRID()));
      Contingency contingency = event.getPayload().getContingency();
      log.info("Contingency: {}, {}", kv("contingencyId",
        contingency.getmRID()), kv("contingencyName", contingency.getName()));

      if ("safety-assessment".equals(event.getPayload().getState())) {
        dynamicSafetyAnalyzer.processAnalyzer(event);
      }
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
    }
  }
}
