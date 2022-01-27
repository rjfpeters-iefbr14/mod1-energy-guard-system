package tld.yggdrasill.services.agm;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.rest.CamundaBpmRestInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;

@SpringBootTest(
//  webEnvironment = SpringBootTest.WebEnvironment.NONE
  properties = {
    "camunda.bpm.generate-unique-process-engine-name=true",
    // this is only needed if a SpringBootProcessApplication
    // is used for the test
    "camunda.bpm.generate-unique-process-application-name=true",
    "spring.datasource.generate-unique-name=true"
    // additional properties...
    // "camunda.bpm.eventing.execution=true",
    // "camunda.bpm.eventing.history=true",
    // "camunda.bpm.eventing.task=true"
  }
)
public class AGMApplicationTest {

  @Autowired
  private ProcessEngine processEngine;

  @Autowired
  RuntimeService runtimeService;

//  @MockBean
//  CamundaBpmRestInitializer restInitializer;

  @BeforeEach
  public void setUp() {
    init(processEngine);
  }

  @Autowired
  private ApplicationContext context;

  @Test
  public void should_load_configuration_without_failures() {
    assertThat(this.context).isNotNull();
  }
}
