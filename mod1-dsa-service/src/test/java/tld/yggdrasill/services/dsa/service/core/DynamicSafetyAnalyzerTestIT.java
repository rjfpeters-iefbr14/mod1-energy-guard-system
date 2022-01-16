package tld.yggdrasill.services.dsa.service.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import tld.yggdrasill.services.cgs.model.Contingency;
import tld.yggdrasill.services.cgs.model.GridServiceEvent;
import tld.yggdrasill.services.cgs.model.Payload;
import tld.yggdrasill.services.dsa.client.GridServiceProducerClient;
import tld.yggdrasill.services.dsa.client.contingency.ContingencyClient;
import tld.yggdrasill.services.dsa.client.contingency.model.ContingencyResponse;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.waitAtMost;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest(
  properties = {
    "spring.kafka.bootstrap.servers=${embedded.kafka.brokerList}",
    "embedded.kafka.topicsToCreate=embedded-test-topic"
  }
)
@ExtendWith(MockitoExtension.class)
public class DynamicSafetyAnalyzerTestIT {

  @Autowired
  protected AdminClient adminClient;

  @Autowired
  private GridServiceProducerClient producer;

  @MockBean ContingencyClient contingencyClient;

  @Value("${app.kafka.producer.topic}")
  private String topic;

  protected void assertThatTopicExists(String topicName) throws Exception {
    ListTopicsResult result = adminClient.listTopics();
    Set<String> topics = result.names().get(10, TimeUnit.SECONDS);
    assertThat(topics).filteredOn(topic -> topic.equals(topicName)).isNotEmpty();
  }

  @Test
  public void should_successfully_created_topic() throws Exception {
    assertThatTopicExists(topic);
  }

  @Test()
  public void should_successfully_sending_event_and_receive_with_assessment_processing() throws JsonProcessingException {
    //-- given
    GridServiceEvent event = new GridServiceEvent();
    Instant createdAt = Instant.now();

    event.setMessageId(UUID.randomUUID().toString());
    event.setCreatedDateTime(createdAt.getEpochSecond());
    event.setProducerId("dsa-embedded-test");

    String contingencyId = UUID.randomUUID().toString();
    Contingency contingency = new Contingency()
      .withmRID(contingencyId)
      .withName("ST-location-somewhere");

    event.withPayload(new Payload()
      .withmRID(UUID.randomUUID().toString())
      .withContingency(contingency)
      .withState("safety-assessment")
    );

    when(contingencyClient.getContingencyById(contingencyId)).thenReturn(
      ContingencyResponse.builder().mRID(contingencyId).name("ST-location-somewhere").build());

    //- when
    producer.send(event);

    //-- then
    waitAtMost(3, TimeUnit.SECONDS)
      .untilAsserted(() -> then(verify(contingencyClient, times(1)).getContingencyById(contingencyId)));
  }

  @TestConfiguration
  public static class KafkaTestConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    public Map<String, Object> consumerConfigs() {
      Map<String, Object> config = new HashMap<>(
        kafkaProperties.buildConsumerProperties()
      );
      config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
      config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
      log.info("Using TestConfiguration - consumerConfig");
      return config;
    }

    @Bean
    AdminClient adminClient() {
      Map<String, Object> config = new HashMap<>(
        kafkaProperties.buildAdminProperties()
      );
      log.info("bootstrap-servers: {}", bootstrapServers);
      return AdminClient.create(config);
    }
  }
}
