package tld.yggdrasill.services.gcm.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Name {

  @Schema(name = "@type")
  private static CimType type = CimType.NAME;

  // This attribute need to be names 'name' according to CIM standards
  @SuppressWarnings("java:S1700")
  private String name;

  private NameType nameType;

  @JsonProperty(value = "@type", index = 0)
  public CimType getType() {
    return type;
  }
}
