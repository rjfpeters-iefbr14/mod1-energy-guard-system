package tld.yggdrasill.services.pcm.client.contingency.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contingency {

  @JsonProperty(value = "mRID", index = 0)
  private String mRID;

  private String name;

  private String description;

//  private List<Name> names;
//  private List<ContingencyEquipment> contingencyEquipments;
//  private TopologicalIsland topologicalIsland;
//  private List<ProductGroup> productGroups;

  private OffsetDateTime expectedStartDateTime;
  private OffsetDateTime expectedEndDateTime;
}

