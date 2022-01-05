package tld.yggdrasill.services.gcm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(
  classes = GCMApplicationIT.TestConfiguration.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = {
    "embedded.mongo.database=deo-system",

    "embedded.vault.path=secret/deo-employee-service",
    "embedded.vault.secrets.spring.data.mongodb.username=${embedded.mongodb.username}",
    "embedded.vault.secrets.spring.data.mongodb.password=${embedded.mongodb.password}"
  })
@AutoConfigureMockMvc
public class GCMApplicationIT {

    @Autowired
    private ApplicationContext context;

    @Test
    public void should_load_configuration_without_failures(){
        assertThat(this.context).isNotNull();
    }

    @EnableAutoConfiguration
    @Configuration
    static class TestConfiguration {
    }
}
