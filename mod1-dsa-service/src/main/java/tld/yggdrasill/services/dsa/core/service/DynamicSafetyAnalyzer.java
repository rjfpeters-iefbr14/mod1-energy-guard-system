package tld.yggdrasill.services.dsa.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tld.yggdrasill.services.cgs.model.Activity;
import tld.yggdrasill.services.cgs.model.End;
import tld.yggdrasill.services.cgs.model.GridServiceEvent;
import tld.yggdrasill.services.cgs.model.Links;
import tld.yggdrasill.services.cgs.model.MetaInf;
import tld.yggdrasill.services.cgs.model.Payload;
import tld.yggdrasill.services.cgs.model.SafetyDossier;
import tld.yggdrasill.services.dsa.client.GridServiceProducerClient;
import tld.yggdrasill.services.dsa.client.contingency.ContingencyClient;
import tld.yggdrasill.services.dsa.client.contingency.model.ContingencyResponse;
import tld.yggdrasill.services.dsa.core.process.SafetyGuardEventState;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
public class DynamicSafetyAnalyzer {

  private final GridServiceProducerClient kafkaProducer;

  private final SafetyAndBalanceDeterminer congestionDeterminer;
  private final ContingencyClient contingencyClient;

  @Value("${info.app.name:undefined}")
  private String producerId;


  public DynamicSafetyAnalyzer(GridServiceProducerClient kafkaProducer,
    SafetyAndBalanceDeterminer congestionDeterminer,
    ContingencyClient contingencyClient) {
    this.kafkaProducer = kafkaProducer;
    this.congestionDeterminer = congestionDeterminer;
    this.contingencyClient = contingencyClient;
  }

  public void processAnalyzer(GridServiceEvent event) throws JsonProcessingException {
    LocalDateTime startAtTime = Instant.now().atOffset(ZoneOffset.UTC).toLocalDateTime();

    String mRID = (String) event.getPayload().getContingency().getmRID();
    ContingencyResponse contingency = contingencyClient.getContingencyById(mRID);
    log.info("Contingency: {} -> {}", kv("contingencyId",mRID),contingency.toString());

    SafetyGuardEventState state = congestionDeterminer.execute(event);

    UUID uuid = UUID.randomUUID();
    Instant createdAt = Instant.now();
    LocalDateTime dateTime = createdAt.atOffset(ZoneOffset.UTC).toLocalDateTime();

    event.setMessageId(uuid.toString());
    event.setCreatedDateTime(createdAt.getEpochSecond());
    event.setProducerId(producerId);

    MetaInf metaInf;
    if (state == SafetyGuardEventState.NO_CONGESTION_DETECTED) {
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
      .withState(state.getState())
      .withMetaInf(metaInf);

    URI location =
      ServletUriComponentsBuilder.fromPath("http://dsa-service.docker.svc/safety-dossiers")
        .queryParam("caseId",payload.getmRID()).build().toUri();
    Links links = new Links()
      .withSafetyDossier(new SafetyDossier().withHref(location.toString()));

    kafkaProducer.send(event);
  }
}
