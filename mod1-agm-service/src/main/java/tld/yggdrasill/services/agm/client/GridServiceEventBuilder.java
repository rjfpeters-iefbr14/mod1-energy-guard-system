package tld.yggdrasill.services.agm.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tld.yggdrasill.services.agm.core.model.ContingencyCaseType;
import tld.yggdrasill.services.agm.core.model.SafetyGuardEventState;
import tld.yggdrasill.services.cgs.model.Contingency;
import tld.yggdrasill.services.cgs.model.GridServiceEvent;
import tld.yggdrasill.services.cgs.model.MetaInf;
import tld.yggdrasill.services.cgs.model.Payload;
import tld.yggdrasill.services.cgs.model.Scenario;
import tld.yggdrasill.services.cgs.model.Start;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
public class GridServiceEventBuilder {

  public static GridServiceEvent buildEvent(String producerId, String contingencyId, String contingencyName, String businessKey) {
    UUID uuid = UUID.randomUUID();
    Instant createdAt = Instant.now();
    GridServiceEvent event = new GridServiceEvent();

    event.setMessageId(uuid.toString());
    event.setCreatedDateTime(createdAt.getEpochSecond());
    event.setProducerId(producerId);

    log.info("Message: {}. {}",
      kv("messageId", event.getMessageId()),
      kv("createdDateTime",event.getCreatedDateTime())
    );

    LocalDateTime dateTime = createdAt.atOffset(ZoneOffset.UTC).toLocalDateTime();

    List<Scenario> scenarios = new ArrayList();
    scenarios.add(new Scenario()
      .withmRID(UUID.randomUUID().toString())
      .withType(ContingencyCaseType.CONTINGENCY_MITIGATION.getType())
    );

    Contingency contingency = new Contingency()
      .withmRID(contingencyId)
      .withName(contingencyName);

    event.withPayload(new Payload()
      .withmRID(businessKey)
      .withName("guard location")
      .withContingency(contingency)
      .withState(SafetyGuardEventState.SAFETY_ASSESSMENT.getState())
      .withMetaInf(new MetaInf()
        .withStart(new Start()
          .withStarter(producerId)
          .withTrigger("trigger")
          .withTime(dateTime.format(DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC)))
        )
      )
      .withScenarios(scenarios)
    );

    log.info("Case {}, {}",
      kv("caseId", event.getPayload().getmRID()),
      kv("caseName", event.getPayload().getName())
    );

    return event;
  }
}
