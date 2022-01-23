package tld.yggdrasill.services.dsa.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tld.yggdrasill.services.dsa.core.model.SafetyDossier;

import java.util.List;
import java.util.UUID;

@Repository
public interface SafetyDossierRepository extends MongoRepository<SafetyDossier, UUID> {

  List findAllByContingencyId(UUID contingencyId);

  List findAllByCaseId(UUID caseId);
}
