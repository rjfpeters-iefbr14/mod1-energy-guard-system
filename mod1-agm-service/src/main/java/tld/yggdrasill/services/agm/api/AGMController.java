package tld.yggdrasill.services.agm.api;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tld.yggdrasill.services.agm.api.common.exceptions.BadProcessDefinitionRequest;
import tld.yggdrasill.services.agm.api.model.ContingencyGuardRequest;
import tld.yggdrasill.services.agm.api.model.ContingencyGuardResponse;
import tld.yggdrasill.services.agm.config.AGMApplicationConstants;
import tld.yggdrasill.services.agm.core.model.ProcessDefinition;
import tld.yggdrasill.services.agm.core.service.ProcessDefinitionService;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/contingencies")
public class AGMController {
  public static MediaType DEFAULT_APPLICATION_JSON_VALUE =
    MediaType.valueOf("application/vnd.contingency-definition.v1+json");

  private final ProcessDefinitionService processDefinitionService;

  private final ModelMapper modelMapper;

  public AGMController(ProcessDefinitionService processDefinitionService) {
    this.processDefinitionService = processDefinitionService;

    modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    modelMapper.typeMap(ProcessDefinition.class, ContingencyGuardRequest.class);
  }

//  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = DEFAULT_APPLICATION_JSON_CONTINGENCY_VALUE)
  @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ContingencyGuardResponse> createContingencyGuard(
    @RequestBody final ContingencyGuardRequest contingencyGuardRequest) {
    log.info("ContingencyGuard: {}", contingencyGuardRequest.toString());

    ProcessDefinition processDefinition;
    try {
      processDefinition = modelMapper.map(contingencyGuardRequest,ProcessDefinition.class);

      switch (processDefinition.getProductGroup()) {
        case "D" -> processDefinition.setSpecificationFileName(
          AGMApplicationConstants.CONTINGENCY_VERIFY_GUARD_EVENT_BPMN);
        case "C" -> processDefinition.setSpecificationFileName(
          AGMApplicationConstants.CONTINGENCY_SAFETY_GUARD_EVENT_BPMN);
        default -> throw new BadProcessDefinitionRequest("unknown productGroup");
      }

      processDefinition = processDefinitionService.activate(processDefinition);
    } catch (IOException e) {
      throw new BadProcessDefinitionRequest(e.getMessage());
    }

    URI location;
    Optional<String> value = Optional.ofNullable(processDefinition.getCamundaProcessDefinitionId());
    if (value.isPresent()) {
      location = ServletUriComponentsBuilder.fromCurrentRequest()
        //-- current path is /contingencies
        .path("/../process-definition/{id}")
        .buildAndExpand(value.get())
        .toUri();
    } else {
      throw new BadProcessDefinitionRequest("CamundaProcessDefinitionId is not available.");
    }

    ContingencyGuardResponse response = ContingencyGuardResponse.builder()
      .camundaDeploymentId(processDefinition.getCamundaDeploymentId())
      .camundaProcessDefinitionId(processDefinition.getCamundaProcessDefinitionId())
      .build();

    return ResponseEntity.created(location).contentType(DEFAULT_APPLICATION_JSON_VALUE).location(location).body(response);
  }
}
