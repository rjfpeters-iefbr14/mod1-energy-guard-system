package tld.yggdrasill.services.dsa.client.contingency.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
@Slf4j
public class FeignClientConfiguration {
  private static final String CONTINGENCY_CLIENT_ID = "contingency-service";

  private final String DEFAULT_APPLICATION_JSON_CONTINGENCY_VALUE = "application/vnd.contingency.v1+json";

  @Bean
  feign.Logger.Level feignLoggerLevel() { return feign.Logger.Level.FULL;  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return new ContingencyErrorDecoder();
  }

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      requestTemplate.header("Content-Type", "application/json");
      requestTemplate.header("Accept", DEFAULT_APPLICATION_JSON_CONTINGENCY_VALUE);
    };
  }

//  @Bean
//  public Decoder feignDecoder() {
//    ObjectMapper objectMapper = new ObjectMapper();
//    objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
//    objectMapper.registerModule(new JavaTimeModule());
//    HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
//    ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
//    return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
//  }

}
