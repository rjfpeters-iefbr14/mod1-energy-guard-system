package tld.yggdrasill.services.agm.client.contingency;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tld.yggdrasill.services.agm.client.contingency.config.exceptions.BadRequestException;
import tld.yggdrasill.services.agm.client.contingency.config.exceptions.ContingencyNotFoundException;
import tld.yggdrasill.services.agm.client.contingency.model.ActuatorHealthResponse;
import tld.yggdrasill.services.agm.client.contingency.model.Contingency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ContingencyClientTest {
  @RegisterExtension
  static WireMockExtension CONTINGENCY_SERVICE = WireMockExtension.newInstance()
    .options(WireMockConfiguration.wireMockConfig().port(8094))
    .build();

  @Autowired
  private ContingencyClient contingencyClient;

  @Test
  void should_successful_return_health() {
    String responseBody = "{ \"status\": \"UP\"}";
    CONTINGENCY_SERVICE.stubFor(
      WireMock.get("/manage/health").willReturn(WireMock.okJson(responseBody)));

    ActuatorHealthResponse contingency = contingencyClient.health();

    assertThat(contingency.getStatus()).isNotNull().isEqualTo("UP");
  }

  @Test
  void should_successful_return_contingency() throws Exception {
    String responseBody = "{ \"mRID\": \"828bc3cb-52f0-482b-8247-d3db5c87c941\", \"name\": \"RS Neerijnen\"}";
    CONTINGENCY_SERVICE.stubFor(
      WireMock.get("/828bc3cb-52f0-482b-8247-d3db5c87c941").willReturn(WireMock.okJson(responseBody)));

    Contingency contingency = contingencyClient.getContingencyById("828bc3cb-52f0-482b-8247-d3db5c87c941");

    assertThat(contingency.getMRID()).isNotNull();
    assertThat(contingency.getName()).isEqualTo("RS Neerijnen");
  }


  @Test
  void should_successful_return_contingency_not_found_exception() {
    String responseBody = "{\"type\":\"https://yggdrasill.tld/contingencies/not-found\",\"title\":\"Contingency\",\"status\":404,\"detail\":\"'bc070161-bb2c-4d2c-8119-4d409e4c4f08' not found\"}";
    CONTINGENCY_SERVICE.stubFor(
      WireMock.get("/bc070161-bb2c-4d2c-8119-4d409e4c4f08")
        .willReturn(WireMock.aResponse().withStatus(404).withBody(responseBody)));

    assertThatThrownBy(() -> {
      contingencyClient.getContingencyById("bc070161-bb2c-4d2c-8119-4d409e4c4f08");
    }).isInstanceOf(ContingencyNotFoundException.class)
      .hasMessageContaining("404")
      .hasMessageContaining("bc070161-bb2c-4d2c-8119-4d409e4c4f08");
  }
}
