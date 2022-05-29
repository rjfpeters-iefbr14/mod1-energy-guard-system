package tld.yggdrasill.services.gcm.core.model;

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
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Document(collection = "contingencies")
@NoArgsConstructor
@AllArgsConstructor
public class Contingency {

  @CreatedDate
  @Builder.Default private Instant createdAt = Instant.now();

  @LastModifiedDate
  private Instant modifiedAt;

  @Id
  private UUID mRID;

  @Indexed(direction = IndexDirection.ASCENDING)
  private String name;

  private String description;

  private List<Name> names;

  private List<ContingencyEquipment> contingencyEquipments;

  private TopologicalIsland topologicalIsland;

  private List<ProductGroup> productGroups;

  private OffsetDateTime expectedStartDateTime;

  private OffsetDateTime expectedEndDateTime;

  private ContingencyStatus status;

  private String camundaDeploymentId;

  private String camundaProcessDefinitionId;

  public static class ContingencyBuilder {};
}

