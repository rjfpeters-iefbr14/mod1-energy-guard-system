package tld.yggdrasill.services.agm.api;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tld.yggdrasill.services.agm.api.common.exceptions.BadProcessDefinitionRequest;
import tld.yggdrasill.services.agm.api.model.ContingencyGuardRequest;
import tld.yggdrasill.services.agm.api.model.ContingencyGuardResponse;
import tld.yggdrasill.services.agm.config.AGMApplicationConstants;
import tld.yggdrasill.services.agm.core.model.ProcessDefinition;
import tld.yggdrasill.services.agm.core.service.ProcessDefinitionService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@Path("/contingencies")
public class AGMController {
  private final String DEFAULT_APPLICATION_JSON_CONTINGENCY_VALUE = "application/vnd.contingency-definition.v1+json";

  private final ProcessEngine camunda;

  private final ProcessDefinitionService processDefinitionService;

  public AGMController(ProcessEngine camunda,
    ProcessDefinitionService processDefinitionService) {
    this.camunda = camunda;
    this.processDefinitionService = processDefinitionService;
  }

  @GET
  @Produces(DEFAULT_APPLICATION_JSON_CONTINGENCY_VALUE)
  public Response getList() {

    var list = camunda.getHistoryService().
      createHistoricProcessInstanceQuery().processDefinitionKey(AGMApplicationConstants.GUARD_BPMN).list();
    log.info("Found {} process instances.", list.size());
    List<String> pidList = new ArrayList<>();
    list.forEach(e -> pidList.add(e.getRootProcessInstanceId()));
    return Response.ok().entity(pidList).build();
  }

  @POST
  @Consumes(APPLICATION_JSON_VALUE)
  @Produces(DEFAULT_APPLICATION_JSON_CONTINGENCY_VALUE)
  public Response createContingencyGuard(
    @RequestBody final ContingencyGuardRequest contingencyGuardRequest) {
    log.info("{}", contingencyGuardRequest.toString());

    ProcessDefinition processDefinition;
    try {
      processDefinition = processDefinitionService.activate(ProcessDefinition.builder()
        .contingencyId(contingencyGuardRequest.getContingencyId())
        .contingencyName(contingencyGuardRequest.getContingencyName())
        .specificationFileName(AGMApplicationConstants.GUARD_BPMN_FILE)
        .build()
      );
    } catch (IOException e) {
      throw new BadProcessDefinitionRequest(e.getMessage());
    }

    URI location;
    Optional<String> value = Optional.ofNullable(processDefinition.getCamundaProcessDefinitionId());
    if (value.isPresent()) {
      location = ServletUriComponentsBuilder.fromCurrentRequest()
        //-- current path is /resources/contingencies
        .path("/../../resources/job-definition/{id}")
        .buildAndExpand(value.get())
        .toUri();
    } else {
      throw new BadProcessDefinitionRequest("CamundaProcessDefinitionId is not available.");
    }

    ContingencyGuardResponse response = ContingencyGuardResponse.builder()
      .bpmnProcessDefinitionId(processDefinition.getCamundaProcessDefinitionId())
      .camundaDeploymentId(processDefinition.getCamundaDeploymentId())
      .camundaProcessDefinitionId(processDefinition.getCamundaProcessDefinitionId())
      .build();

    return Response.accepted().location(location).entity(response).build();
  }
}
