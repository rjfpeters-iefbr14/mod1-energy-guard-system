package tld.yggdrasill.services.gcm.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tld.yggdrasill.services.gcm.core.model.ActivePower;
import tld.yggdrasill.services.gcm.core.model.Contingency;
import tld.yggdrasill.services.gcm.core.model.ContingencyEquipment;
import tld.yggdrasill.services.gcm.core.model.ContingencyStatus;
import tld.yggdrasill.services.gcm.core.model.LifeCycleStatus;
import tld.yggdrasill.services.gcm.core.model.Limit;
import tld.yggdrasill.services.gcm.core.model.Measurement;
import tld.yggdrasill.services.gcm.core.model.Name;
import tld.yggdrasill.services.gcm.core.model.NameType;
import tld.yggdrasill.services.gcm.core.model.ProductGroup;
import tld.yggdrasill.services.gcm.core.model.Terminal;
import tld.yggdrasill.services.gcm.core.model.UnitMultiplier;
import tld.yggdrasill.services.gcm.core.model.UnitSymbol;
import tld.yggdrasill.services.gcm.core.repository.ContingencyRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Configuration
@Slf4j
public class LoadSampleDatabase {
  final
  ContingencyRepository repository;

  public LoadSampleDatabase(ContingencyRepository repository) {
    this.repository = repository;
  }

  private static Contingency.ContingencyBuilder buildContingency(UUID uuid)  {
    List<Name> equipmentNames =
      List.of(new Name("12", new NameType("SFID", "Short Term Forecasting id")));

    List<Terminal> terminals =
      List.of(new Terminal(UUID.randomUUID(), List.of(new Measurement(UUID.randomUUID()))));

    List<Limit> limits =
      List.of(new Limit("", "", new ActivePower(123.0, UnitMultiplier.M, UnitSymbol.W)));

    List<ContingencyEquipment> equipments =
      List.of(
        new ContingencyEquipment(
          UUID.randomUUID(), "RS Neerijnen", "", equipmentNames, terminals, limits));

    List<Name> contingencyNames =
      List.of(
        new Name("1 123 45", new NameType("stationIdentifier", "Station identifier")));

    var status =
      new ContingencyStatus(LifeCycleStatus.ACTIVE, "Remark", OffsetDateTime.now(ZoneOffset.UTC));

    return Contingency.builder()
      .mRID(uuid)
      .name("Sample Station")
      .description("Contingency on Sample Station")
      .names(contingencyNames)
      .contingencyEquipments(equipments)
      .expectedStartDateTime(OffsetDateTime.now(ZoneOffset.UTC))
      .expectedEndDateTime(OffsetDateTime.now(ZoneOffset.UTC))
      .productGroups(List.of(ProductGroup.GOPACS))
      .status(status);
  }

  @Bean
  CommandLineRunner initDatabase() {
    return args -> {
      log.info("Preloading: " + repository.save(
        buildContingency(UUID.fromString("bc070161-bb2c-4d2c-8119-4d409e4c4f04"))
        .name("RS Neerijnen")
        .description("Contingency RS Neerijnen").build()));
      log.info("Preloading: " + repository.save(
        buildContingency(UUID.fromString("15769db8-dc0d-474e-a0bf-ee2a3c5942f8"))
        .name("RS Wieringenwerf")
        .description("Contingency RS Wieringenwerf").build()));
      log.info("Preloading: " + repository.save(
        buildContingency(UUID.fromString("25115a71-2761-4399-a5c4-600d1bda367f"))
        .name("OS Zevenhuizen")
        .description("Contingency OS Zevenhuizen").build()));
      log.info("Preloading: " + repository.save(
        buildContingency(UUID.fromString("6e125579-3e85-4983-b4e8-e680c95435ad"))
          .name("OS Hoofddorp")
          .description("Contingency OS Hoofddorp").build()));
      log.info("Preloading: " + repository.save(buildContingency(UUID.randomUUID()).build()));
      log.info("Preloading: " + repository.save(buildContingency(UUID.randomUUID()).build()));
    };
  }
}
