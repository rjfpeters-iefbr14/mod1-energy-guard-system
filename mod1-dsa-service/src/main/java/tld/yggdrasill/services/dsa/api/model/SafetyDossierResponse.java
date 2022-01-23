package tld.yggdrasill.services.dsa.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SafetyDossierResponse {

  @JsonProperty(value = "mRID", index = 0)
  private UUID mRID;

  private String name;

  private String description;

  private UUID contingencyId;

  private UUID caseId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  private OffsetDateTime startAtTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  private OffsetDateTime endAtTime;
}
