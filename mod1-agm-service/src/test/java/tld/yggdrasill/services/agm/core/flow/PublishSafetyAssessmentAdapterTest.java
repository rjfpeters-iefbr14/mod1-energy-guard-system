package tld.yggdrasill.services.agm.core.flow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.extension.mockito.CamundaMockito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tld.yggdrasill.services.agm.client.GridServiceProducerClient;
import tld.yggdrasill.services.agm.client.contingency.ContingencyClient;
import tld.yggdrasill.services.agm.client.contingency.model.ContingencyResponse;
import tld.yggdrasill.services.cgs.model.GridServiceEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PublishSafetyAssessmentAdapterTest {

  @Mock ContingencyClient contingencyClient;
  @Mock
  GridServiceProducerClient kafkaProducer;
  @InjectMocks PublishSafetyAssessmentAdapter task;

  private DelegateExecution execution;

  @BeforeEach
  void setUp() {
    execution = CamundaMockito.delegateExecutionFake();
  }

  @Test
  public void should_successful_execute_task_safety_assessment() throws Exception {
    //-- given
    ArgumentCaptor<GridServiceEvent> eventCaptor = ArgumentCaptor.forClass(GridServiceEvent.class);

    //-- when
    execution.setVariable("contingencyId","bc070161-bb2c-4d2c-8119-4d409e4c4f04");
    execution.setVariable("contingencyName","SubStation Sample");

    when(contingencyClient.getContingencyById("bc070161-bb2c-4d2c-8119-4d409e4c4f04")).thenReturn(
      ContingencyResponse.builder().mRID("bc070161-bb2c-4d2c-8119-4d409e4c4f04").name("SubStation Sample").build());

    task.execute(execution);

    //-- then
    verify(kafkaProducer).send(eventCaptor.capture());
    GridServiceEvent event = eventCaptor.getValue();
    assertThat(event).isNotNull();

    assertThat(execution.getProcessBusinessKey()).isNotNull();
  }
}
