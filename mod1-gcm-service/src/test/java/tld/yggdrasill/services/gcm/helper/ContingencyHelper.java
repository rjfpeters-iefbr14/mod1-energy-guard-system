package tld.yggdrasill.services.gcm.helper;

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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContingencyHelper {

  public static final String DEFAULT_CONTINGENCY_ID = "bc070161-bb2c-4d2c-8119-4d409e4c4f04";

  public static Contingency getOneContingency() {
    return buildContingency(1,UUID.randomUUID());
  }

  public static List<Contingency> getMultipleContingencies(int numberOfContingencies) {
    List<Contingency> returnList = new ArrayList<>();
    for (int i = 1; i <= numberOfContingencies; i++) {
      returnList.add(buildContingency(i,UUID.randomUUID()));
    }
    return returnList;
  }

  public static Contingency getContingencyById(UUID uuid) {
    return buildContingency(1, uuid);
  }

  public static Contingency getContingency() {
    return buildContingency(1, UUID.fromString(ContingencyHelper.DEFAULT_CONTINGENCY_ID));
  }

  private static Contingency buildContingency(int index, UUID uuid)  {
    List<Name> equipmentNames =
        List.of(new Name("12" + index, new NameType("SFID", "Short Term Forecasting id")));

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
            new Name("1 123 45" + index, new NameType("stationIdentifier", "Station identifier")));

    var status =
        new ContingencyStatus(LifeCycleStatus.ACTIVE, "Remark", OffsetDateTime.now(ZoneOffset.UTC));

    return Contingency.builder()
      .mRID(uuid)
      .name("RS Neerijnen")
      .description("Contingency RS Neerijnen")
      .names(contingencyNames)
      .contingencyEquipments(equipments)
      .expectedStartDateTime(OffsetDateTime.now(ZoneOffset.UTC))
      .expectedEndDateTime(OffsetDateTime.now(ZoneOffset.UTC))
      .productGroups(List.of(ProductGroup.GOPACS))
      .status(status)
      .build();
  }
}
