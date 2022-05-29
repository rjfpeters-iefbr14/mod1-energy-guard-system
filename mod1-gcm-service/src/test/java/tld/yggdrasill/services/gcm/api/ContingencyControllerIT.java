package tld.yggdrasill.services.gcm.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tld.yggdrasill.services.gcm.api.model.v1.ContingencyRequest;
import tld.yggdrasill.services.gcm.core.model.Contingency;
import tld.yggdrasill.services.gcm.core.model.ContingencyStatus;
import tld.yggdrasill.services.gcm.core.repository.ContingencyRepository;
import tld.yggdrasill.services.gcm.helper.ContingencyHelper;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = {
    "embedded.mongo.database=gcm-system"
  })
@AutoConfigureMockMvc
public class ContingencyControllerIT {

  private static final UUID DEFAULT_CONTINGENCY_ID = UUID.randomUUID();

  @Autowired
  MockMvc mvc;

  @Autowired
  ContingencyRepository repository;

  @BeforeEach
  public void ingestSampleData() {
    repository.save(ContingencyHelper.getContingencyById(DEFAULT_CONTINGENCY_ID));
  }

  @AfterEach
  public void cleanSampleData() {
    repository.deleteById(DEFAULT_CONTINGENCY_ID);
  }

  @Test
  void should_successfully_get_a_list_of_all_contingencies() throws Exception {
    this.mvc.perform(MockMvcRequestBuilders
        .get("/") //
        .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(ContingencyController.DEFAULT_APPLICATION_JSON_VALUE))
      .andExpect(MockMvcResultMatchers.jsonPath("$.contingencies").exists())
      .andExpect(MockMvcResultMatchers.jsonPath("$.contingencies[*].mRID").isNotEmpty());
  }

  @Test
  void should_successfully_get_a_single_contingency() throws Exception {
    Contingency contingency = repository.save(ContingencyHelper.getContingency());

    this.mvc.perform(MockMvcRequestBuilders
        .get("/" + contingency.getMRID()) //
        .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(ContingencyController.DEFAULT_APPLICATION_JSON_VALUE))
      .andExpect(MockMvcResultMatchers.jsonPath("$.mRID").isNotEmpty());
  }

  @Test
  void should_fail_when_get_an_unknown_contingency() throws Exception {
    this.mvc.perform(MockMvcRequestBuilders
        .get("/unknown")
        .accept(MediaType.APPLICATION_JSON))
      .andDo(print()) //
      .andExpect(status().is4xxClientError())
      .andExpect(header().string("Content-Type", "application/problem+json"));
  }

  @Test
  void should_successfully_delete_a_contingency() throws Exception {
    Contingency contingency = repository.save(ContingencyHelper.getOneContingency());

    this.mvc.perform(MockMvcRequestBuilders
        .delete("/" + contingency.getMRID()) //
        .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isNoContent());
  }

  @Test
  void should_successfully_add_a_contingency_when_valid_request() throws Exception {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    modelMapper.typeMap(ContingencyRequest.class, Contingency.class)
      .addMappings(mapper -> mapper.skip(Contingency::setMRID));

    Contingency contingency = ContingencyHelper.getOneContingency();
    ContingencyRequest request = modelMapper.map(contingency, ContingencyRequest.class);

    ObjectMapper mapper = JsonMapper.builder()
      .addModule(new JavaTimeModule())
      .build();
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

    this.mvc.perform(MockMvcRequestBuilders
        .post("/")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(ow.writeValueAsString(contingency)))
      .andDo(print()) //
      .andExpect(status().isCreated())
      .andExpect(content().contentType(ContingencyController.DEFAULT_APPLICATION_JSON_VALUE))
      .andExpect(MockMvcResultMatchers.jsonPath("$.mRID").exists());
  }

  @Test
  void should_fail_to_add_a_contingency_when_invalid_request() throws Exception {
    ContingencyRequest contingency = new ContingencyRequest();
    contingency.setStatus(new ContingencyStatus());

    ObjectMapper mapper = new ObjectMapper();
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

    this.mvc.perform(MockMvcRequestBuilders
        .post("/")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(ow.writeValueAsString(contingency)))
      .andDo(print()) //
      .andExpect(status().is4xxClientError())
      .andExpect(header().string("Content-Type", "application/problem+json"));
  }

  @Test
  void should_successfully_search_for_contingencies() throws Exception {
    Contingency contingency = ContingencyHelper.getOneContingency();
    contingency.setName("ST-sample");
    repository.save(contingency);

    this.mvc.perform(MockMvcRequestBuilders
        .get("/") //
        .param("search", "name:ST-sample")
        .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType(ContingencyController.DEFAULT_APPLICATION_JSON_VALUE))
      .andExpect(MockMvcResultMatchers.jsonPath("$.contingencies").exists())
      .andExpect(MockMvcResultMatchers.jsonPath("$.contingencies[*].mRID").isNotEmpty());
  }
}
