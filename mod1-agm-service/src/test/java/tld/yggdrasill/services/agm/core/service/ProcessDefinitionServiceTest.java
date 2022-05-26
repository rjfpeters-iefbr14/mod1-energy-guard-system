package tld.yggdrasill.services.agm.core.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tld.yggdrasill.services.agm.core.model.ProcessDefinition;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(
  // webEnvironment = WebEnvironment.NONE,
  properties = { //
    "camunda.bpm.generate-unique-process-engine-name=true",
    // this is only needed if a SpringBootProcessApplication
    // is used for the test
    "camunda.bpm.generate-unique-process-application-name=true",
    "spring.datasource.generate-unique-name=true",
    //
    "camunda.bpm.job-execution.enabled=false", //
    "camunda.bpm.auto-deployment-enabled=false"
  })
class ProcessDefinitionServiceTest {

  @Autowired
  private ProcessDefinitionService processDefinitionService;

  @Test
  void should_successful_activate_verify_workflow() throws IOException {

    ProcessDefinition processDefinition = ProcessDefinition.builder()
      .contingencyId(UUID.randomUUID().toString())
      .contingencyName("SS Sample")
      .timerCycle("R/PT3M")
      .specificationFileName("bpmn//contingency-verify-guard-event.bpmn")
      .build();

    ProcessDefinition result = processDefinitionService.activate(processDefinition);

    assertThat(result.getCamundaProcessDefinitionId()).isNotNull();
  }

  @Test
  void should_successful_activate_safety_workflow() throws IOException {

    ProcessDefinition processDefinition = ProcessDefinition.builder()
      .contingencyId(UUID.randomUUID().toString())
      .contingencyName("SS Sample")
      .timerCycle("R/PT3M")
      .specificationFileName("bpmn//contingency-safety-guard-event.bpmn")
      .build();

    ProcessDefinition result = processDefinitionService.activate(processDefinition);

    assertThat(result.getCamundaProcessDefinitionId()).isNotNull();
  }
}
