package tld.yggdrasill.services.dsa.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
class DSAControllerTestIT {

  @Autowired
  MockMvc mvc;

  @Test
  void should_successfully_send_a_message() throws Exception {
    this.mvc.perform(MockMvcRequestBuilders
        .get("/producer")
        .param("message", "simulation")
        .accept(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isNoContent());
  }
}
