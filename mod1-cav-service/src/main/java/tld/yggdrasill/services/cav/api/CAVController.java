package tld.yggdrasill.services.cav.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tld.yggdrasill.services.cav.client.GridServiceProducerClient;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class CAVController {

  private final GridServiceProducerClient kafkaProducer;

  public CAVController(GridServiceProducerClient kafkaProducer) {
    this.kafkaProducer = kafkaProducer;
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

}
