package tld.yggdrasill.services.agm.core.process;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.run.ProcessRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tld.yggdrasill.services.agm.client.GridServiceProducerClient;
import tld.yggdrasill.services.cgs.model.GridServiceEvent;

import java.net.URISyntaxException;
import java.util.Map;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.mockito.Mockito.*;

@Slf4j
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
@Deployment(resources = {"bpmn//contingency-verify-event.bpmn"})
//@ExtendWith(MockitoExtension.class)
public class ContingencyAgreementVerificationProcessTest {

  public static final String PROCESS_KEY = "VerificationProcess";
  public static final String START_EVENT_VERIFY = "StartEvent_Verification";
  public static final String END_EVENT_VERIFY_COMPLETED = "EndEvent_VerifyCompleted";

  @RegisterExtension
  static WireMockExtension CONTINGENCY_SERVICE = WireMockExtension.newInstance()
    .options(WireMockConfiguration.wireMockConfig().port(8094))
    .build();

  private final ProcessScenario verificationProcess = mock(ProcessScenario.class);

  @MockBean
  private GridServiceProducerClient kafkaProducer;

  @Autowired
  private RuntimeService runtimeService;

  private Map<String, Object> variables;

  @BeforeEach
  public void setup() throws URISyntaxException {
    MockitoAnnotations.openMocks(this);

    variables = Variables.createVariables()
      .putValue("contingencyId", "828bc3cb-52f0-482b-8247-d3db5c87c941")
      .putValue("contingencyName", "SubStation Sample");

    String responseBody = "{ \"mRID\": \"828bc3cb-52f0-482b-8247-d3db5c87c941\", \"name\": \"RS Neerijnen\"}";
    CONTINGENCY_SERVICE.stubFor(
      WireMock.get("/828bc3cb-52f0-482b-8247-d3db5c87c941").willReturn(WireMock.okJson(responseBody)));
  }

  @Test
  public void should_successfully_start_process() throws InterruptedException {
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, variables);
    assertThat(processInstance).isNotNull();
    runtimeService.deleteProcessInstance(processInstance.getId(), "JUnit test");
  }

  @Test
  public void should_successfully_execute_contingency_verification() {
    //-- given
    ArgumentCaptor<GridServiceEvent> eventCaptor = ArgumentCaptor.forClass(GridServiceEvent.class);

//    when(verificationProcess.waitsAtSendTask("SendTaskStartVerification")).thenReturn((task) -> {
//      task.complete();
//    });

    when(verificationProcess.waitsAtReceiveTask("ReceiveTaskWaitForVerification")).thenReturn((task) -> {
      task.receive();
    });

    when(verificationProcess.waitsAtTimerIntermediateEvent(anyString())).thenReturn((processInstance) -> {
      processInstance.defer("R/PT10M", () -> {
        fail("Timer should have fired in the meanwhile");
      });
    });

    ProcessRunner.ExecutableRunner.StartingByStarter starter = Scenario.run(verificationProcess) //
      .startBy(() -> {
        return runtimeService.startProcessInstanceByKey(PROCESS_KEY,
          variables);
      });

    //-- when
    starter.execute();

    //-- then
    verify(kafkaProducer).send(eventCaptor.capture());
    GridServiceEvent event = eventCaptor.getValue();
    assertThat(event).isNotNull();
    assertThat(event.getPayload().getState()).isNotNull().isEqualTo("contingency-verifier");

    verify(verificationProcess).hasFinished(END_EVENT_VERIFY_COMPLETED);
  }
}
