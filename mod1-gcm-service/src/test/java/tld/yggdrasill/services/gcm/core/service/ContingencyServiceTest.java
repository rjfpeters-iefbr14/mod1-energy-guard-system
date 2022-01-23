package tld.yggdrasill.services.gcm.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tld.yggdrasill.services.gcm.core.model.Contingency;
import tld.yggdrasill.services.gcm.core.repository.ContingencyRepository;
import tld.yggdrasill.services.gcm.helper.ContingencyHelper;

//@ExtendWith(SpringExtension.class) -> as of spring-boot 2.1 no longer needed
class ContingencyServiceTest {

  @Mock private ContingencyRepository contingencyRepository;

  @InjectMocks private ContingencyService contingencyService;

  @Test
  void getContingencies() {
    final int NUM_CONTINGENCIES = 2;

    when(contingencyRepository.findAll())
      .thenReturn(ContingencyHelper.getMultipleContingencies(NUM_CONTINGENCIES));

    List<Contingency> contingencies = contingencyService.getContingencies();

    assertEquals(NUM_CONTINGENCIES, contingencies.size());
  }

  @Test
  void getContingency() {
    Contingency oneContingency = ContingencyHelper.getOneContingency();
    final UUID mRID = oneContingency.getMRID();

    when(contingencyRepository.findById(mRID)).thenReturn(Optional.of(oneContingency));

    Optional<Contingency> contingency = contingencyService.getContingency(mRID);

    assertTrue(contingency.isPresent());
    assertEquals(oneContingency, contingency.get());
  }

  @Test
  void createContingency() {
    Contingency oneContingency = ContingencyHelper.getOneContingency();

    when(contingencyRepository.save(oneContingency)).thenReturn(oneContingency);

    Contingency contingency = contingencyService.createContingency(oneContingency);

    assertNotNull(contingency);
    assertEquals(oneContingency, contingency);
  }

  @Test
  void updateContingency() {
    Contingency oneContingency = ContingencyHelper.getOneContingency();
    final UUID MRID = oneContingency.getMRID();

    when(contingencyRepository.findById(MRID)).thenReturn(Optional.of(oneContingency));
    when(contingencyRepository.save(oneContingency)).thenReturn(oneContingency);

    Optional<Contingency> contingency = contingencyService.updateContingency(MRID, oneContingency);

    assertTrue(contingency.isPresent());
    assertEquals(oneContingency, contingency.get());
  }

  @Test
  void updateContingencyNotFound() {
    Contingency oneContingency = ContingencyHelper.getOneContingency();
    final UUID MRID = oneContingency.getMRID();

    when(contingencyRepository.findById(MRID)).thenReturn(Optional.empty());

    Optional<Contingency> contingency = contingencyService.updateContingency(MRID, oneContingency);

    assertTrue(contingency.isEmpty());
  }

  @Test
  void deleteContingency() {
    Contingency oneContingency = ContingencyHelper.getOneContingency();
    final UUID MRID = oneContingency.getMRID();

    when(contingencyRepository.findById(MRID)).thenReturn(Optional.of(oneContingency));

    Optional<Contingency> contingency = contingencyService.deleteContingency(MRID);

    verify(contingencyRepository, times(1)).deleteById(MRID);
    assertTrue(contingency.isPresent());
    assertEquals(oneContingency, contingency.get());
  }

  @Test
  void deleteContingencyNotFound() {
    Contingency oneContingency = ContingencyHelper.getOneContingency();
    final UUID MRID = oneContingency.getMRID();

    when(contingencyRepository.findById(MRID)).thenReturn(Optional.empty());

    Optional<Contingency> contingency = contingencyService.deleteContingency(MRID);

    verify(contingencyRepository, times(0)).deleteById(MRID);
    assertTrue(contingency.isEmpty());
  }
}
