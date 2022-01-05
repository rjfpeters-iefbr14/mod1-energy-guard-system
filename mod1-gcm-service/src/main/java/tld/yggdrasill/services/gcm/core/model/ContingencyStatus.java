package tld.yggdrasill.services.gcm.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContingencyStatus {
  @Schema(description = "Current status of this contingency")
  private LifeCycleStatus value;

  @Schema(description = "Remarks for this contingency")
  private String remark;

  @Schema(description = "Last updated datetime (ISO-8601) for this contingency")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  private OffsetDateTime updatedDateTime;
}
