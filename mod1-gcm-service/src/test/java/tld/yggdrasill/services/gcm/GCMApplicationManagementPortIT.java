package tld.yggdrasill.services.gcm;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.web.server.LocalManagementPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = {
    "embedded.mongo.database=deo-system",

    "embedded.vault.path=secret/deo-employee-service",
    "embedded.vault.secrets.spring.data.mongodb.username=${embedded.mongodb.username}",
    "embedded.vault.secrets.spring.data.mongodb.password=${embedded.mongodb.password}"
  })
@TestPropertySource(
  properties = {
    "management.server.port=0",
    "management.endpoints.web.base-path=/manage"
})
public class GCMApplicationManagementPortIT {

  @LocalManagementPort
  int managementPort;

  @Test
  public void should_successfully_get_actuator_health_information() {
    ResponseEntity<Map> entity = new TestRestTemplate().getForEntity(
            "http://localhost:" + managementPort + "/manage/health", Map.class);
    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

    @Test
    public void should_successfully_get_actuator_information() {
        ResponseEntity<Map> entity = new TestRestTemplate().getForEntity(
          "http://localhost:" + managementPort + "/manage/info", Map.class);
      assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
