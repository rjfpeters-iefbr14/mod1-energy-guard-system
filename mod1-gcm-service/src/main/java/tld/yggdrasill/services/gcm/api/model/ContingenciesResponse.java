package tld.yggdrasill.services.gcm.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import tld.yggdrasill.services.gcm.core.model.Contingency;

import java.util.List;

@Data
@AllArgsConstructor
public class ContingenciesResponse {
  private List<ContingencyResponse> Contingencies;
}
