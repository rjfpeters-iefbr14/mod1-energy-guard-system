package tld.yggdrasill.services.dsa.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tld.yggdrasill.services.cgs.model.Activity;
import tld.yggdrasill.services.cgs.model.End;
import tld.yggdrasill.services.cgs.model.GridServiceEvent;
import tld.yggdrasill.services.cgs.model.MetaInf;
import tld.yggdrasill.services.cgs.model.Payload;
import tld.yggdrasill.services.dsa.client.GridServiceProducerService;
import tld.yggdrasill.services.dsa.client.contingency.ContingencyClient;
import tld.yggdrasill.services.dsa.client.contingency.model.Contingency;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
public class DynamicSafetyAnalyzer {

  private final GridServiceProducerService kafkaProducer;

  private final ContingencyClient contingencyClient;

  @Value("${info.app.name:undefined}")
  private String producerId;

  private String determineAnalysisResult(GridServiceEvent event) {
    String sample = String.valueOf(event.getMessageId().toString().charAt(0));
    int remainder = Integer.parseInt(sample, 16) % 3; //determines if 3 is a factor
    if (remainder == 0) {
      return  "congestion-detected";
    }
    return "no-congestion-detected";
  }

  public DynamicSafetyAnalyzer(GridServiceProducerService kafkaProducer,
    ContingencyClient contingencyClient) {
    this.kafkaProducer = kafkaProducer;
    this.contingencyClient = contingencyClient;
  }

  public void processAnalyzer(GridServiceEvent event) throws JsonProcessingException {
    LocalDateTime startAtTime = Instant.now().atOffset(ZoneOffset.UTC).toLocalDateTime();

    String mRID = (String) event.getPayload().getContingency().getmRID();
    Contingency contingency = contingencyClient.getContingencyById(mRID);
    log.info("Contingency: {} -> {}", kv("contingencyId",mRID),contingency.toString());

    //-- sample analyzing this event
    try{Thread.sleep(4000);}catch(InterruptedException e){
      log.warn(e.getMessage());
    }

    UUID uuid = UUID.randomUUID();
    Instant createdAt = Instant.now();
    LocalDateTime dateTime = createdAt.atOffset(ZoneOffset.UTC).toLocalDateTime();

    event.setMessageId(uuid.toString());
    event.setCreatedDateTime(createdAt.getEpochSecond());
    event.setProducerId(producerId);

    String state = determineAnalysisResult(event);
    MetaInf metaInf;
    if ("no-congestion-detected".equalsIgnoreCase(state)) {
      metaInf =  new MetaInf()
        .withEnd(new End()
          .withEnder(producerId)
          .withTime(dateTime.format(DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC)))
        );
    } else {
      metaInf =  new MetaInf()
        .withActivity(new Activity()
          .withAgent(producerId)
          .withStartAtTime(startAtTime.format(DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC)))
          .withEndAtTime(dateTime.format(DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC)))
        );
    }

    Payload payload = event.getPayload();
    payload
      .withState(state)
      .withMetaInf(metaInf);

    kafkaProducer.send(event);
  }
}
