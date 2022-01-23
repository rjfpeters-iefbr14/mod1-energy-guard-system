package tld.yggdrasill.services.dsa.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@Document(collection = "safety-dossier")
@NoArgsConstructor
@AllArgsConstructor
public class SafetyDossier {

  @CreatedDate
  @Builder.Default private Instant createdAt = Instant.now();

  @LastModifiedDate
  private Instant modifiedAt;

  @Id
  private UUID mRID;

  @Indexed(direction = IndexDirection.ASCENDING)
  private String name;

  private String description;

  @Indexed
  private UUID contingencyId;

  @Indexed
  private UUID caseId;

  private OffsetDateTime startAtTime;
  private OffsetDateTime endAtTime;
}
