package tld.yggdrasill.services.gcm.api;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tld.yggdrasill.services.gcm.api.common.exceptions.ContingencyNotFound;
import tld.yggdrasill.services.gcm.api.common.validator.annotation.isValidUUID;
import tld.yggdrasill.services.gcm.api.model.v1.ContingenciesResponse;
import tld.yggdrasill.services.gcm.api.model.v1.ContingencyEquipmentsResponse;
import tld.yggdrasill.services.gcm.api.model.v1.ContingencyRequest;
import tld.yggdrasill.services.gcm.api.model.v1.ContingencyResponse;
import tld.yggdrasill.services.gcm.api.model.v1.TopologicalIslandResponse;
import tld.yggdrasill.services.gcm.core.model.Contingency;
import tld.yggdrasill.services.gcm.core.model.ContingencyEquipment;
import tld.yggdrasill.services.gcm.core.model.TopologicalIsland;
import tld.yggdrasill.services.gcm.core.service.ContingencyService;
import tld.yggdrasill.services.gcm.core.utils.BasicPredicateBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Validated
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ContingencyController {
  public static MediaType DEFAULT_APPLICATION_JSON_VALUE =
    MediaType.valueOf("application/vnd.contingency.v1+json");

  public static MediaType NEXTGEN_APPLICATION_JSON_VALUE =
    MediaType.valueOf("application/vnd.contingency.v2+json");

  private final ContingencyService contingencyService;

  private final ModelMapper modelMapper;

  <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
    return source
      .stream()
      .map(element -> modelMapper.map(element, targetClass))
      .collect(Collectors.toList());
  }

  public ContingencyController(ContingencyService contingencyService) {
    this.contingencyService = contingencyService;

    this.modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    modelMapper.typeMap(ContingencyRequest.class, Contingency.class)
      .addMappings(mapper -> mapper.skip(Contingency::setMRID));

    modelMapper.typeMap(Contingency.class, ContingencyResponse.class);
  }


  @PostMapping(value = "/", consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<ContingencyResponse> create(@RequestBody @Valid ContingencyRequest request) {
    log.info("Contingency request: {}", kv("contingencyName", request.getName()));

    Contingency contingency = modelMapper.map(request, Contingency.class);
    assert contingency != null : "Mapping request to contingency failed";
    log.info("Contingency add: {}", contingency.toString());

    contingencyService.createContingency(contingency);

    URI location;
    Optional<UUID> value = Optional.ofNullable(contingency.getMRID());
    if (value.isPresent()) {
      location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(value.get())
        .toUri();
    } else {
      throw new IllegalArgumentException();
    }

    ContingencyResponse response = modelMapper.map(contingency, ContingencyResponse.class);
    return ResponseEntity.created(location).contentType(DEFAULT_APPLICATION_JSON_VALUE).body(response);
  }

  @GetMapping(value = "/")
  public ResponseEntity<ContingenciesResponse> search(@RequestHeader MultiValueMap<String, String> headers,
    @RequestParam(value = "search", required = false) String search) {
    headers.forEach((key, value) -> {
      log.debug(String.format(
        "Header '%s' = %s", key, value.stream().collect(Collectors.joining("|"))));
    });
    List<Contingency> contingencies = new ArrayList<>();
    if (search != null) {
      log.info(String.format("Contingency search: %s.", search));
      BasicPredicateBuilder builder = new BasicPredicateBuilder();
      builder.from(search.replaceAll("[()]", ""));
      BooleanExpression expression = builder.build(Contingency.class);
      Iterable<Contingency> contingencyIterable = contingencyService.search(expression);
      contingencyIterable.forEach(contingencies::add);
    } else {
      log.info("Contingency find all.");
      contingencies = contingencyService.findAll();
    }

    if (contingencies.isEmpty())
      throw new ContingencyNotFound();

    List<ContingencyResponse> contingencyResponses = mapList(contingencies,ContingencyResponse.class);
    return ResponseEntity.ok().contentType(DEFAULT_APPLICATION_JSON_VALUE).body(new ContingenciesResponse(contingencyResponses));
  }


  //TODO:- add caching information
  @GetMapping(value = "/{id}")
  public ResponseEntity<ContingencyResponse> getById(@RequestHeader MultiValueMap<String, String> headers,
    @PathVariable("id") @isValidUUID String id) {
    headers.forEach((key, value) -> {
      log.debug(String.format(
        "Header '%s' = %s", key, value.stream().collect(Collectors.joining("|"))));
    });

    log.info("Contingency findById: {}", kv("mRID", id));
    Contingency contingency = contingencyService.findById(UUID.fromString(id))
      .orElseThrow(() -> new ContingencyNotFound(id));

    ContingencyResponse response = modelMapper.map(contingency, ContingencyResponse.class);
    return ResponseEntity.ok().contentType(DEFAULT_APPLICATION_JSON_VALUE).body(response);
  }

  //TODO:- add caching information
  @GetMapping(value = "/{id}/contingency-equipments")
  public ResponseEntity<ContingencyEquipmentsResponse> getEquipmentsById(@PathVariable("id") @isValidUUID String id) {
    log.info("Contingency (contingency-equipments) findById: {}", kv("mRID", id));
    Contingency contingency = contingencyService.findById(UUID.fromString(id))
      .orElseThrow(() -> new ContingencyNotFound(id));

    List<ContingencyEquipment> equipments = contingency.getContingencyEquipments();

    ContingencyEquipmentsResponse response = new ContingencyEquipmentsResponse(equipments);
    return ResponseEntity.ok().contentType(DEFAULT_APPLICATION_JSON_VALUE).body(response);
  }

  @GetMapping(value = "/{id}/topological-island")
  public ResponseEntity<TopologicalIslandResponse> getTopologicalIslandById(@PathVariable("id") @isValidUUID String id) {
    log.info("Contingency (topological-island) findById: {}", kv("mRID", id));

    Contingency contingency = contingencyService.findById(UUID.fromString(id))
      .orElseThrow(() -> new ContingencyNotFound(id));

    TopologicalIsland island = contingency.getTopologicalIsland();

    TopologicalIslandResponse response = new TopologicalIslandResponse();
    response.setTerminals(island.getTerminals());
    return ResponseEntity.ok().contentType(DEFAULT_APPLICATION_JSON_VALUE).body(response);
  }

  @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<ContingencyResponse> update(@PathVariable("id") @isValidUUID String id,
    @RequestBody @Valid ContingencyRequest request) {
    log.info("Contingency findById: {}", kv("mRID", id));
    Contingency contingency = contingencyService.findById(UUID.fromString(id))
      .orElseThrow(() -> new ContingencyNotFound(id));

    contingencyService.updateContingency(UUID.fromString(id), contingency);

    modelMapper.typeMap(Contingency.class, ContingencyResponse.class);
    ContingencyResponse response = modelMapper.map(contingency, ContingencyResponse.class);

    return ResponseEntity.ok().contentType(DEFAULT_APPLICATION_JSON_VALUE).body(response);
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") @isValidUUID String id) {
    log.info("Contingency findById: {}", kv("mRID", id));
    Contingency contingency = contingencyService.findById(UUID.fromString(id))
      .orElseThrow(() -> new ContingencyNotFound(id));
    contingencyService.deleteContingency(UUID.fromString(id));
  }
}
