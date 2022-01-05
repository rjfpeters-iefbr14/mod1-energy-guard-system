package tld.yggdrasill.services.gcm.core.repository.listener;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import tld.yggdrasill.services.gcm.core.model.Contingency;

import java.util.UUID;

@Component
public class GenerateUUIDContingencyListener extends AbstractMongoEventListener<Contingency> {

  @Override
  public void onBeforeConvert(BeforeConvertEvent<Contingency> event) {
    var contingency = event.getSource();

    if (contingency.getMRID() == null) {
      contingency.setMRID(UUID.randomUUID());
    }
  }
}
