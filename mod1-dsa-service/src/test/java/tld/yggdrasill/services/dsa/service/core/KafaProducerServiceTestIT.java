package tld.yggdrasill.services.dsa.service.core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import tld.yggdrasill.services.dsa.client.GridServiceProducerService;

@SpringBootTest(
  properties = {
    "spring.kafka.bootstrap.servers=${embedded.kafka.brokerList}",
    "spring.kafka.producer.bootstrap.servers=${embedded.kafka.brokerList}"
  }
)
public class KafaProducerServiceTestIT {

  @Autowired
  public KafkaTemplate<Integer, String> template;

//  @Autowired
//  private KafkaConsumerClient consumer;

  @Autowired
  private GridServiceProducerService producer;

  @Value("${app.kafka.producer.topic}")
  private String topic;

  @Test
  public void givenEmbeddedKafkaBroker_whenSendingtoDefaultTemplate_thenMessageReceived() throws Exception {
    this.template.send(topic, "Sending with default template");
//    consumer.getLatch().await(10000, TimeUnit.MILLISECONDS);
//    assertThat(consumer.getLatch().getCount(), equalTo(0L));
//
//    assertThat(consumer.getPayload(), containsString("embedded-test-topic"));
  }

  @Test
  public void givenEmbeddedKafkaBroker_whenSendingToSimpleProducer_thenMessageReceived() throws Exception {
    producer.send("Sending with our own simple KafkaProducer");
//    latch.await(10000, TimeUnit.MILLISECONDS);

    //    assertThat(consumer.getLatch().getCount(), equalTo(0L));
    //    assertThat(consumer.getPayload(), containsString("embedded-test-topic"));
  }
}
