package tld.yggdrasill.services.dsa.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tld.yggdrasill.services.dsa.client.GridServiceProducerClient;
import tld.yggdrasill.services.dsa.client.contingency.ContingencyClient;
import tld.yggdrasill.services.dsa.client.contingency.model.ContingencyResponse;

import static net.logstash.logback.argument.StructuredArguments.kv;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class DSAController {

  private final GridServiceProducerClient kafkaProducer;

  private final ContingencyClient contingencyClient;

  public DSAController(GridServiceProducerClient kafkaProducer,
    ContingencyClient contingencyClient) {
    this.kafkaProducer = kafkaProducer;
    this.contingencyClient = contingencyClient;
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

  @GetMapping(value = "/contingencies/{mRID}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getContingency(@PathVariable String mRID) {
    log.info("Contingency: {}", kv("contingencyId",mRID));

    ContingencyResponse contingency = contingencyClient.getContingencyById(mRID);
    log.info("Contingency: {} -> {}", kv("contingencyId",mRID),contingency.toString());

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
