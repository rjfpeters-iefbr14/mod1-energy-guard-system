package tld.yggdrasill.services.dsa.api;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tld.yggdrasill.services.dsa.api.common.exceptions.SafetyDossierNotFound;
import tld.yggdrasill.services.dsa.api.common.validator.annotation.isValidUUID;
import tld.yggdrasill.services.dsa.api.model.SafetyDossierResponse;
import tld.yggdrasill.services.dsa.api.model.SafetyDossiersResponse;
import tld.yggdrasill.services.dsa.client.GridServiceProducerClient;
import tld.yggdrasill.services.dsa.client.contingency.ContingencyClient;
import tld.yggdrasill.services.dsa.client.contingency.model.ContingencyResponse;
import tld.yggdrasill.services.dsa.core.model.SafetyDossier;
import tld.yggdrasill.services.dsa.core.service.SafetyDossierService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Validated
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class DSAController {
  public static MediaType DEFAULT_APPLICATION_JSON_VALUE = MediaType.valueOf("application/vnd.safety-dossier.v1+json");

  private final GridServiceProducerClient kafkaProducer;
  private final ContingencyClient contingencyClient;

  private final SafetyDossierService safetyDossierService;

  private final ModelMapper modelMapper;

  <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
    return source
      .stream()
      .map(element -> modelMapper.map(element, targetClass))
      .collect(Collectors.toList());
  }

  public DSAController(GridServiceProducerClient kafkaProducer,
    ContingencyClient contingencyClient,
    SafetyDossierService safetyDossierService) {
    this.kafkaProducer = kafkaProducer;
    this.contingencyClient = contingencyClient;
    this.safetyDossierService = safetyDossierService;

    this.modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    modelMapper.typeMap(SafetyDossier.class, SafetyDossierResponse.class);
  }

  @PostMapping(value = "/producer", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<?> add(@RequestBody String request) {
    log.info("NetService : {}", request);
    kafkaProducer.send(request);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping(value = "/producer", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<?> sendMessage(@RequestParam("message") String message) {
    log.info("Message to send {}",message);
    kafkaProducer.send(message);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping(value = "/contingencies/{contingencyId}/proxy", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getContingency(@PathVariable("contigencyId") @isValidUUID String contingencyId) {
    log.info("Contingency: {}", kv("contingencyId",contingencyId));

    ContingencyResponse contingency = contingencyClient.getContingencyById(contingencyId);
    log.info("Contingency: {} -> {}", kv("contingencyId",contingencyId),contingency.toString());

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping(value = "/safety-dossiers/{mRID}")
  public ResponseEntity<SafetyDossierResponse> getById(@PathVariable("mRID") @isValidUUID String mRID) {
    log.info("SafetyDossier findBy: {}", kv("mRID", mRID));

    SafetyDossier dossier = safetyDossierService.findById(UUID.fromString(mRID))
      .orElseThrow(() -> new SafetyDossierNotFound(mRID));

    SafetyDossierResponse response = modelMapper.map(dossier,SafetyDossierResponse.class);
    return ResponseEntity.ok().contentType(DEFAULT_APPLICATION_JSON_VALUE).body(response);
  }

  @GetMapping(value = "/safety-dossiers")
  public ResponseEntity<SafetyDossiersResponse> getByCaseId(@RequestParam(value = "caseId") @isValidUUID String caseId) {
    log.info("SafetyDossier findBy: {}", kv("caseId", caseId));

    List<SafetyDossier> dossiers = safetyDossierService.findAllByCaseId(UUID.fromString(caseId));

    if (dossiers.isEmpty())
      throw new SafetyDossierNotFound();

    List<SafetyDossierResponse> response = mapList(dossiers,SafetyDossierResponse.class);
    return ResponseEntity.ok().contentType(DEFAULT_APPLICATION_JSON_VALUE).body(new SafetyDossiersResponse(response));
  }
}
