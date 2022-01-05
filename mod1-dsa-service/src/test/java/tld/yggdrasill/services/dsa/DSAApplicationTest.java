package tld.yggdrasill.services.dsa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
  classes = DSAApplicationTest.TestConfiguration.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class DSAApplicationTest {

  @Autowired
  private ApplicationContext context;

  @Test
  public void should_load_configuration_without_failures() {
    assertThat(this.context).isNotNull();
  }

  @EnableAutoConfiguration
  @Configuration
  static class TestConfiguration {
  }
}
