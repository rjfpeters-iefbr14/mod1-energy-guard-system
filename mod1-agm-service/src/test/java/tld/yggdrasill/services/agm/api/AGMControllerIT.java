package tld.yggdrasill.services.agm.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.camunda.bpm.engine.RepositoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tld.yggdrasill.services.agm.api.model.ContingencyGuardRequest;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = {
    "camunda.bpm.generate-unique-process-engine-name=true",
    // this is only needed if a SpringBootProcessApplication
    // is used for the test
    "camunda.bpm.generate-unique-process-application-name=true",
    "spring.datasource.generate-unique-name=true",
    // additional properties...
  })
@AutoConfigureMockMvc
public class AGMControllerIT {

  @Autowired
  MockMvc mvc;

  @SpyBean @Autowired
  RepositoryService repositoryService;

  @Test
  void should_successfully_add_a_contingency_guard() throws Exception {

    ContingencyGuardRequest contingencyGuardRequest = ContingencyGuardRequest.builder()
      .contingencyId("bc070161-bb2c-4d2c-8119-4d409e4c4f04")
      .contingencyName("SubStation Sample")
      .timerCycle("R/PT6M")
      .productGroup("C")
      .build();

    ObjectMapper mapper = new ObjectMapper();
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

    mvc.perform(MockMvcRequestBuilders
        .post("/contingencies")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(ow.writeValueAsString(contingencyGuardRequest)))
      .andDo(print()) //
      .andExpect(status().isCreated())
      .andExpect(content().contentType(AGMController.DEFAULT_APPLICATION_JSON_VALUE))
      .andExpect(MockMvcResultMatchers.jsonPath("$.camundaDeploymentId").exists())
      .andExpect(MockMvcResultMatchers.jsonPath("$.camundaProcessDefinitionId").exists());

    verify(repositoryService,atLeastOnce()).createProcessDefinitionQuery();
  }

  @TestConfiguration
  public static class AGMControllerTestConfiguration {
  }
}
