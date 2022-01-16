package tld.yggdrasill.services.dsa.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
  private final String bootstrapServers;
  private final KafkaProperties kafkaProperties;

  @Autowired
  public KafkaProducerConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
    KafkaProperties kafkaProperties) {
    this.bootstrapServers = bootstrapServers;
    this.kafkaProperties = kafkaProperties;
  }

  @Bean
  public Map<String, Object> producerConfigs() {
    Map<String, Object> config = new HashMap<>(
      kafkaProperties.buildProducerProperties()
    );
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    //...
    //config.put(ProducerConfig.LINGER_MS_CONFIG, 10);
    return config;
  }

  @Bean
  public ProducerFactory<String, String> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfigs());
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
