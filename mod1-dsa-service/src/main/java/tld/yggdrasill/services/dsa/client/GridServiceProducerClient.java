package tld.yggdrasill.services.dsa.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tld.yggdrasill.services.cgs.model.GridServiceEvent;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
public class GridServiceProducerClient {

  private final ObjectMapper objectMapper;

  private final KafkaTemplate<String, String> kafkaTemplate;

  @Value("${app.kafka.producer.topic}")
  String topic;

  @Autowired
  public GridServiceProducerClient(ObjectMapper objectMapper,
    KafkaTemplate<String, String> kafkaTemplate) {
    this.objectMapper = objectMapper;
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(GridServiceEvent event) {
    try {
      // avoid too much magic and transform ourselves
      String jsonMessage = objectMapper.writeValueAsString(event);

      //configure objectMapper for pretty input
      //objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

      // wrap into a proper message for the transport (Kafka/Rabbit) and send it
      this.send(jsonMessage);
    } catch (Exception e) {
      throw new RuntimeException("Could not transform and send message due to: " + e.getMessage(), e);
    }
  }

  public void send(String message) {
    this.kafkaTemplate.send(topic, message)
      .addCallback(
        result -> log.info("Publish: {} {}", message, kv("topic",topic)),
        ex -> log.error("Failed to send message", ex)
      );
  }
}
