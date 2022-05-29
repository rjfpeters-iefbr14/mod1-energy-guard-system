package tld.yggdrasill.services.gcm.api.model.v1;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ContingenciesResponse {
  private List<ContingencyResponse> Contingencies;
}
