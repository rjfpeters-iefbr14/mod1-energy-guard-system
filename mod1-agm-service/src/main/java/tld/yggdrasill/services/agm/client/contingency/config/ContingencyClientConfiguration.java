package tld.yggdrasill.services.agm.client.contingency.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ContingencyClientConfiguration {
  private static final String CONTINGENCY_CLIENT_ID = "contingency-service";

  private final String DEFAULT_APPLICATION_JSON_CONTINGENCY_VALUE = "application/vnd.contingency.v1+json";

  @Bean
  public ErrorDecoder contingencyErrorDecoder() {
    return new ContingencyErrorDecoder();
  }

  @Bean
  public RequestInterceptor contingencyRequestInterceptor() {
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
