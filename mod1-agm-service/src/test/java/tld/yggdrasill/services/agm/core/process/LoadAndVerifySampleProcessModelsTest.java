package tld.yggdrasill.services.agm.core.process;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.RequiredHistoryLevel;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;

@Slf4j
@SpringBootTest( // webEnvironment = WebEnvironment.NONE,
  properties = { //
    "camunda.bpm.generate-unique-process-engine-name=true",
    //    // this is only needed if a SpringBootProcessApplication
    //    // is used for the test
    "camunda.bpm.generate-unique-process-application-name=true",
    "spring.datasource.generate-unique-name=true",
    //
    "camunda.bpm.job-execution.enabled=false", //
    "camunda.bpm.auto-deployment-enabled=false" //
  })
public class LoadAndVerifySampleProcessModelsTest {

  @Autowired
  ProcessEngine processEngine;

  @BeforeEach
  public void setUp() {
    init(processEngine);
  }

  @Autowired
  RuntimeService runtimeService;

  @Test
  @Deployment(resources = {"bpmn//order-coffee.bpmn"})
  public void should_successfully_load_order_coffee_process() {
    // Given we create a new process instance
    Map<String, Object> variables = Variables.createVariables()
      .putValue("order","12");
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("order-coffee",variables);

    assertThat(processInstance).hasPassed("make-coffee");
    assertThat(processInstance).hasPassed("pour-coffee-in-cup");
    // Then the process instance should be ended
    assertThat(processInstance).hasPassed("deliver-coffee-order").isEnded();
  }

  @Test
  @Deployment(resources = {"bpmn//sample.bpmn"})
  public void should_successfully_load_sample_process() {
    // Given we create a new process instance
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("sample");
    // Then it should be active
    assertThat(processInstance).isActive();
    // And it should be the only instance
    assertThat(processInstanceQuery().count()).isEqualTo(1);
    // And there should exist just a single task within that process instance
    assertThat(task(processInstance)).isNotNull();

    // When we complete that task
    complete(task(processInstance));
    // Then the process instance should be ended
    assertThat(processInstance).isEnded();
  }
}
