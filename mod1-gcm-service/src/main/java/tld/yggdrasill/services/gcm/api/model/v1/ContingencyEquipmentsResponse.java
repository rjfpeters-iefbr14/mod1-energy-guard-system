package tld.yggdrasill.services.gcm.api.model.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import tld.yggdrasill.services.gcm.core.model.ContingencyEquipment;

import java.util.List;

@Data
@AllArgsConstructor
public class ContingencyEquipmentsResponse {
  private List<ContingencyEquipment> contingencyEquipments;
}
