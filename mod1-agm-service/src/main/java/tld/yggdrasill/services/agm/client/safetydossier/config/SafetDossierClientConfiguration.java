package tld.yggdrasill.services.agm.client.safetydossier.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SafetDossierClientConfiguration {
  private static final String SAFETY_DOSSIER_CLIENT_ID = "safety-dossier-service";

  private final String DEFAULT_APPLICATION_JSON_SAFETY_DOSSIER_VALUE = "application/vnd.safety-dossier.v1+json";

  @Bean
  public ErrorDecoder safetyDossierErrorDecoder() {
    return new SafetyDossierErrorDecoder();
  }

  @Bean
  public RequestInterceptor safetyDossierRequestInterceptor() {
    return requestTemplate -> {
      requestTemplate.header("Content-Type", "application/json");
      requestTemplate.header("Accept", DEFAULT_APPLICATION_JSON_SAFETY_DOSSIER_VALUE);
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
