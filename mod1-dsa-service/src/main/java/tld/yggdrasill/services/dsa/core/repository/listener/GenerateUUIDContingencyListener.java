package tld.yggdrasill.services.dsa.core.repository.listener;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import tld.yggdrasill.services.dsa.core.model.SafetyDossier;

import java.util.UUID;

@Component
public class GenerateUUIDContingencyListener extends AbstractMongoEventListener<SafetyDossier> {

  @Override
  public void onBeforeConvert(BeforeConvertEvent<SafetyDossier> event) {
    var safetyDossier = event.getSource();

    if (safetyDossier.getMRID() == null) {
      safetyDossier.setMRID(UUID.randomUUID());
    }
  }
}
