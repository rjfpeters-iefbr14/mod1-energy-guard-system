package tld.yggdrasill.services.dsa.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tld.yggdrasill.services.dsa.core.model.SafetyDossier;
import tld.yggdrasill.services.dsa.core.repository.SafetyDossierRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class SafetyDossierService {

  private final SafetyDossierRepository safetyDossierRepository;

  public SafetyDossierService(
    SafetyDossierRepository safetyDossierRepository) {
    this.safetyDossierRepository = safetyDossierRepository;
  }

  public SafetyDossier createDossier(SafetyDossier dossier) {
    return safetyDossierRepository.insert(dossier);
  }

  public Optional<SafetyDossier> findById(UUID uuid) {
    return safetyDossierRepository.findById(uuid);
  }

  public List<SafetyDossier> findAllByContingencyId(UUID contingencyId) {
    return safetyDossierRepository.findAllByContingencyId(contingencyId);
  }

  public List<SafetyDossier> findAllByCaseId(UUID contingencyId) {
    return safetyDossierRepository.findAllByCaseId(contingencyId);
  }
}
