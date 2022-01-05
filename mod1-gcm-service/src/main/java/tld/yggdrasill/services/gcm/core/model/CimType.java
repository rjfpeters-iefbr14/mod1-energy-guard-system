package tld.yggdrasill.services.gcm.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CimType {
  @JsonProperty("Analog")
  ANALOG,

  @JsonProperty("Contingency")
  CONTINGENCY,

  @JsonProperty("ContingencyEquipment")
  CONTINGENCY_EQUIPMENT,

  @JsonProperty("Name")
  NAME,

  @JsonProperty("Terminal")
  TERMINAL,

  @JsonProperty("TopologicalIsland")
  TOPOLOGICAL_ISLAND
}
