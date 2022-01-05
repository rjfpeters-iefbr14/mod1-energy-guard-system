package tld.yggdrasill.services.gcm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import tld.yggdrasill.services.gcm.config.converter.DateToOffsetDateTimeConverter;
import tld.yggdrasill.services.gcm.config.converter.OffsetDateTimeToDateConverter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMongoAuditing
public class MongoConfig {
  @Bean
  public MongoCustomConversions customConversions() {
    List<Converter<?, ?>> converterList = new ArrayList<>();
    converterList.add(new DateToOffsetDateTimeConverter());
    converterList.add(new OffsetDateTimeToDateConverter());
    return new MongoCustomConversions(converterList);
  }
}
