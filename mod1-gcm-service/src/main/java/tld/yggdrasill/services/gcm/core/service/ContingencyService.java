package tld.yggdrasill.services.gcm.core.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tld.yggdrasill.services.gcm.core.model.Contingency;
import tld.yggdrasill.services.gcm.core.repository.ContingencyRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.querydsl.core.types.Predicate;

@Service
@AllArgsConstructor
public class ContingencyService {

  private final ContingencyRepository contingencyRepository;

  public List<Contingency> getContingencies() {
    return contingencyRepository.findAll();
  }

  public Optional<Contingency> getContingency(UUID mRID) {
    return contingencyRepository.findById(mRID);
  }

  public Contingency createContingency(Contingency contingency) {
    return saveContingency(contingency);
  }

  public Optional<Contingency> updateContingency(UUID mRID, Contingency updatedContingency) {
    Optional<Contingency> foundContingency = contingencyRepository.findById(mRID);

    if (foundContingency.isEmpty()) {
      return foundContingency;
    }

    updatedContingency.setMRID(foundContingency.get().getMRID());
    var contingency = saveContingency(updatedContingency);

    return Optional.of(contingency);
  }

  public Optional<Contingency> deleteContingency(UUID mRID) {
    Optional<Contingency> foundContingency = contingencyRepository.findById(mRID);

    if (foundContingency.isEmpty()) {
      return foundContingency;
    }
    contingencyRepository.deleteById(mRID);

    return foundContingency;
  }

  public List<Contingency> findAll() {
    return contingencyRepository.findAll();
  }

  public Iterable<Contingency> search(Predicate expression) {
    return contingencyRepository.findAll(expression);
  }

  public Optional<Contingency> findById(UUID uuid) {
    return contingencyRepository.findById(uuid);
  }

  private Contingency saveContingency(Contingency contingency) {
    contingency.getStatus().setUpdatedDateTime(OffsetDateTime.now(ZoneOffset.UTC));
    return contingencyRepository.save(contingency);
  }
}
