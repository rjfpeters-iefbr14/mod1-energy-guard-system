package tld.yggdrasill.services.pcm.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tld.yggdrasill.services.cgs.model.End;
import tld.yggdrasill.services.cgs.model.GridServiceEvent;
import tld.yggdrasill.services.cgs.model.MetaInf;
import tld.yggdrasill.services.cgs.model.Payload;
import tld.yggdrasill.services.pcm.client.GridServiceProducerClient;
import tld.yggdrasill.services.pcm.client.contingency.ContingencyClient;
import tld.yggdrasill.services.pcm.client.contingency.model.ContingencyResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
public class PowerCapacityManager {

  private final ContingencyClient contingencyClient;

  private final GridServiceProducerClient kafkaProducer;

  @Value("${info.app.name:undefined}")
  String producerId;

  public PowerCapacityManager(ContingencyClient contingencyClient,
    GridServiceProducerClient kafkaProducer) {
    this.contingencyClient = contingencyClient;
    this.kafkaProducer = kafkaProducer;
  }

  public void processFulfillment(GridServiceEvent event) throws JsonProcessingException {
    LocalDateTime startAtTime = Instant.now().atOffset(ZoneOffset.UTC).toLocalDateTime();

    String mRID = (String) event.getPayload().getContingency().getmRID();
    ContingencyResponse contingency = contingencyClient.getContingencyById(mRID);
    log.info("Contingency: {} -> {}", kv("contingencyId",mRID),contingency.toString());

    //-- sample processing this event
    try {
      Thread.sleep(4000);} catch (InterruptedException e) {
      log.warn(e.getMessage());
    }

    UUID uuid = UUID.randomUUID();
    Instant createdAt = Instant.now();
    LocalDateTime dateTime = createdAt.atOffset(ZoneOffset.UTC).toLocalDateTime();

    event.setMessageId(uuid.toString());
    event.setCreatedDateTime(createdAt.getEpochSecond());
    event.setProducerId(producerId);

    Payload payload = event.getPayload();
    payload
      .withState("congestion-resolved")
      .withMetaInf(
      new MetaInf()
        .withEnd(new End()
          .withEnder(this.producerId)
          .withTime(dateTime.format(DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC)))
        )
    );

    kafkaProducer.send(event);
  }
}
