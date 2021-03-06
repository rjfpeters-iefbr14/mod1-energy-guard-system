package tld.yggdrasill.services.agm.core.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.TimeCycle;
import org.camunda.bpm.model.bpmn.instance.TimerEventDefinition;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import tld.yggdrasill.services.agm.core.model.ProcessDefinition;
import tld.yggdrasill.services.agm.core.service.helpers.ProcessDefinitionHelper;

import java.io.IOException;
import java.io.InputStream;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@Slf4j
public class ProcessDefinitionService {

  //-- these are identifiers in the bpmn-file -> safety-guard-event.
  public static final String START_FLOW_ACTIVITY = "SendTask_StartContingency";
  public static final String TIMER_EVENT_ID = "StartEvent_Contingency";

  private final RepositoryService repositoryService;

  public ProcessDefinitionService(RepositoryService repositoryService) {
    this.repositoryService = repositoryService;
  }

  private Resource loadContingencyGuardWithClassPathResource(String fileName) {
    return new ClassPathResource(fileName);
  }

  private Process extractAndValidProcessFromModel(BpmnModelInstance modelInstance) {
    ModelElementType processType = modelInstance.getModel().getType(Process.class);
    Process process = (Process) modelInstance.getModelElementsByType(processType).iterator().next();

    if (!process.isExecutable()) {
      throw new IllegalStateException("BPMN Process is not executable");
    }

    if (StringUtils.isBlank(process.getName())) {
      throw new IllegalStateException("BPMN Process name is blank");
    }
    return process;
  }

  public ProcessDefinition activate(ProcessDefinition processDefinition) throws IOException {
    log.info("Request to activate ProcessDefinition : {}", processDefinition);

    InputStream resource = loadContingencyGuardWithClassPathResource(
      processDefinition.getSpecificationFileName()).getInputStream();
    BpmnModelInstance modelInstance = Bpmn.readModelFromStream(resource);
    log.info("ProcessDefinition file '{}' loaded", processDefinition.getSpecificationFileName());

    Process process = extractAndValidProcessFromModel(modelInstance);

    process.setId(ProcessDefinitionHelper.formatBpmnProcessId(processDefinition));
    process.setName(ProcessDefinitionHelper.formatBpmnProcessName(process.getName(), processDefinition));

    if (!process.getDocumentations().isEmpty()) {
      processDefinition.setDescription(process.getDocumentations().iterator().next().getRawTextContent());
    }

    //-- add contingency variables
    log.info("Add contingency variables: {}, {}",
      kv("contingencyId",processDefinition.getContingencyId()),
      kv("contingencyName",processDefinition.getContingencyName()));
    SendTask sendTask = (SendTask) modelInstance.getModelElementById(START_FLOW_ACTIVITY);
    sendTask.builder()
      .camundaInputParameter("contingencyId", processDefinition.getContingencyId())
      .camundaInputParameter("contingencyName", processDefinition.getContingencyName());

    //-- modify the timer definition
    StartEvent startEvent = (StartEvent) modelInstance.getModelElementById(TIMER_EVENT_ID);
    TimerEventDefinition timerEventDefinition = (TimerEventDefinition) startEvent.getEventDefinitions().iterator().next();
    log.info("Modify timer event definition with id: {}",timerEventDefinition.getId());
    TimeCycle timeCycle = (TimeCycle) modelInstance.newInstance(TimeCycle.class);
    timeCycle.setTextContent(processDefinition.getTimerCycle());
    timerEventDefinition.setTimeCycle(timeCycle);
    startEvent.setName("Every: %s".formatted(processDefinition.getTimerCycle()));

    Bpmn.validateModel(modelInstance);

    processDefinition.setBpmnProcessDefinitionId(
      ProcessDefinitionHelper.formatBpmnProcessDefinitionId(processDefinition));

    log.info("Deployment for {}.", processDefinition.getBpmnProcessDefinitionId());
    Deployment camundaDeployment = repositoryService.createDeployment()
      .addModelInstance(processDefinition.getBpmnProcessDefinitionId() + ".bpmn", modelInstance)
      .name(processDefinition.getBpmnProcessDefinitionId())
      .source("process.definition.service")
      .tenantId(processDefinition.getTenantId())
      .deploy();
    processDefinition.setCamundaDeploymentId(camundaDeployment.getId());
    log.info("Deployed under {}.", kv("camundaDeploymentId", processDefinition.getCamundaDeploymentId()));

    org.camunda.bpm.engine.repository.ProcessDefinition camundaProcessDefinition = repositoryService
      .createProcessDefinitionQuery()
      .deploymentId(camundaDeployment.getId())
      .singleResult();

    processDefinition.setCamundaProcessDefinitionId(camundaProcessDefinition.getId());
    log.info("ProcessDefinition activate with {}.",
      kv("camundaProcessDefinitionId", processDefinition.getCamundaProcessDefinitionId()));

    return processDefinition;
  }
}
