package tld.yggdrasill.services.agm.api.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AGMRestConfiguration {

  @Bean
  public RestTemplate createRestTemplate() {
    return new RestTemplate();
  }
}
