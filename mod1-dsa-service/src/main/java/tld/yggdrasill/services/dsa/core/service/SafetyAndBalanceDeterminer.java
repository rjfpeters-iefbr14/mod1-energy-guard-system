package tld.yggdrasill.services.dsa.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tld.yggdrasill.services.cgs.model.GridServiceEvent;
import tld.yggdrasill.services.dsa.core.model.SafetyDossier;
import tld.yggdrasill.services.dsa.core.process.SafetyGuardEventState;

import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
public class SafetyAndBalanceDeterminer {

  private final SafetyDossierService safetyDossierService;

  public SafetyAndBalanceDeterminer(SafetyDossierService safetyDossierService) {
    this.safetyDossierService = safetyDossierService;
  }


  private SafetyGuardEventState determineAnalysisResult(GridServiceEvent event) {
    String sample = String.valueOf(event.getMessageId().toString().charAt(0));
    int remainder = Integer.parseInt(sample, 16) % 3; //determines if 3 is a factor
    if (remainder == 0) {
      return SafetyGuardEventState.CONGESTION_DETECTED;
    }
    return SafetyGuardEventState.NO_CONGESTION_DETECTED;
  }


  public SafetyGuardEventState execute(GridServiceEvent event){

    //-- sample analyzing this event
    try{Thread.sleep(4000);}catch(InterruptedException e){
      log.warn(e.getMessage());
    }

    SafetyGuardEventState state = determineAnalysisResult(event);
    log.info("Analyzer {}",kv("state",state.getState()));
    if (state == SafetyGuardEventState.CONGESTION_DETECTED) {
      SafetyDossier dossier = SafetyDossier.builder()
        .mRID(UUID.randomUUID())
        .contingencyId(UUID.fromString((String) event.getPayload().getContingency().getmRID()))
        .caseId(UUID.fromString((String) event.getPayload().getmRID())).build();
      safetyDossierService.createDossier(dossier);
    }

    return state;
  }
}
