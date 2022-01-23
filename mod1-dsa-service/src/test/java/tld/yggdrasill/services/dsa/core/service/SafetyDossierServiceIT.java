package tld.yggdrasill.services.dsa.core.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tld.yggdrasill.services.dsa.core.model.SafetyDossier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
  properties = {
    "embedded.mongo.database=deo-system"
})
public class SafetyDossierServiceIT {

  @Autowired
  private SafetyDossierService safetyDossierService;

  @Test
  public void should_successfully_insert_safety_dossier() {
    SafetyDossier dossier = safetyDossierService.createDossier(
      SafetyDossier.builder()
      .contingencyId(UUID.randomUUID())
      .caseId(UUID.randomUUID()).build());
    assertThat(dossier.getMRID()).isNotNull();
  }

  @Test
  public void should_successfully_findBy_contingency() {
    UUID contingencyId = UUID.randomUUID();

    safetyDossierService.createDossier(
      SafetyDossier.builder()
        .contingencyId(contingencyId)
        .caseId(UUID.randomUUID()).build());

    List<SafetyDossier> list = safetyDossierService.findAllByContingencyId(contingencyId);
    assertThat(list).isNotNull().hasSize(1);
  }
}
