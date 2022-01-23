package tld.yggdrasill.services.dsa.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tld.yggdrasill.services.dsa.core.model.SafetyDossier;
import tld.yggdrasill.services.dsa.core.repository.SafetyDossierRepository;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = {
    "embedded.mongo.database=dsa-system"
  }
)
@AutoConfigureMockMvc
class DSAControllerTestIT {

  private static final UUID DEFAULT_MRID_ID = UUID.randomUUID();
  private static final UUID DEFAULT_CASE_ID = UUID.randomUUID();

  @Autowired
  MockMvc mvc;

  @Autowired
  SafetyDossierRepository repository;

  @BeforeEach
  public void ingestSampleData() {
    repository.save(
      SafetyDossier.builder()
        .mRID(DEFAULT_MRID_ID)
        .contingencyId(UUID.randomUUID())
        .caseId(DEFAULT_CASE_ID).build());
  }

  @AfterEach
  public void cleanSampleData() {
    repository.deleteById(DEFAULT_MRID_ID);
  }

  @Test
  void should_successfully_send_a_message() throws Exception {
    this.mvc.perform(MockMvcRequestBuilders
        .get("/producer")
        .param("message", "simulation")
        .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isNoContent());
  }

  @Test
  void should_fail_when_get_an_unknown_safety_dossier() throws Exception {
    this.mvc.perform(MockMvcRequestBuilders
        .get("/safety-dossiers/{id}","unknown")
        .accept(MediaType.APPLICATION_JSON))
      .andDo(print()) //
      .andExpect(status().is4xxClientError())
      .andExpect(header().string("Content-Type", "application/problem+json"));
  }

  @Test
  void should_successfully_get_a_single_safety_dossier() throws Exception {
    SafetyDossier dossier = repository.save(
      SafetyDossier.builder().contingencyId(UUID.randomUUID()).caseId(DEFAULT_CASE_ID).build());

    this.mvc.perform(MockMvcRequestBuilders
        .get("/safety-dossiers/{id}", String.valueOf(dossier.getMRID())) //
        .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(DSAController.DEFAULT_APPLICATION_JSON_VALUE))
      .andExpect(MockMvcResultMatchers.jsonPath("$.mRID").isNotEmpty());
  }

  @Test
  void should_successfully_get_safety_dossiers_by_case() throws Exception {
    SafetyDossier dossier = repository.save(
      SafetyDossier.builder().contingencyId(UUID.randomUUID()).caseId(DEFAULT_CASE_ID).build());

    this.mvc.perform(MockMvcRequestBuilders
        .get("/safety-dossiers") //
        .param("caseId", String.valueOf(DEFAULT_CASE_ID))
        .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(DSAController.DEFAULT_APPLICATION_JSON_VALUE))
      .andExpect(MockMvcResultMatchers.jsonPath("$.safetyDossiers").exists())
      .andExpect(MockMvcResultMatchers.jsonPath("$.safetyDossiers[*].mRID").isNotEmpty());
  }
}
