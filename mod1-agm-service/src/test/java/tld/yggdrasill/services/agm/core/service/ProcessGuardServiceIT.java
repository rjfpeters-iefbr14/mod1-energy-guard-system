package tld.yggdrasill.services.agm.core.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import tld.yggdrasill.services.agm.client.GridServiceProducerClient;
import tld.yggdrasill.services.agm.client.contingency.ContingencyClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest(
  properties = {
    "spring.kafka.bootstrap-servers=${embedded.kafka.brokerList}",
    "embedded.kafka.topicsToCreate=embedded-test-topic",
  }
)
public class ProcessGuardServiceIT {

  @Autowired
  protected AdminClient adminClient;

  @Autowired
  private GridServiceProducerClient producer;

  @MockBean
  ContingencyClient contingencyClient;

  @Value("${app.kafka.producer.topic}")
  private String topic;

  protected void assertThatTopicExists(String topicName) throws Exception {
    ListTopicsResult result = adminClient.listTopics();
    Set<String> topics = result.names().get(10, TimeUnit.SECONDS);
    Assertions.assertThat(topics).filteredOn(topic -> topic.equals(topicName)).isNotEmpty();
  }

  @Test
  public void should_successfully_create_topic() throws Exception {
    assertThatTopicExists(topic);
  }

  @TestConfiguration
  public static class KafkaTestConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Autowired
    private KafkaProperties kafkaProperties;

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
