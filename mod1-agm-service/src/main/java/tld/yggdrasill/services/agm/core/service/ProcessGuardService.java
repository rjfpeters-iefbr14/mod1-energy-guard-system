package tld.yggdrasill.services.agm.core.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tld.yggdrasill.services.agm.core.model.ActivityDecisionMap;
import tld.yggdrasill.services.agm.core.model.GuardEventState;
import tld.yggdrasill.services.cgs.model.GridServiceEvent;
import tld.yggdrasill.services.cgs.model.Payload;

import static net.logstash.logback.argument.StructuredArguments.kv;


@Slf4j
@Service
public class ProcessGuardService {

  private ProcessEngine camunda;

  @Autowired
  public ProcessGuardService(ProcessEngine camunda) {
    this.camunda = camunda;
  }

  public void processEvent(String caseId, GridServiceEvent event) {
    try {
      log.info("Correlating {} to waiting flow instance", kv("caseId", caseId));

      Payload payload = event.getPayload();
      String state = payload.getState();

      correlateSafetyAssessment(caseId, state);

      correlateAgreementVerifier(caseId, state);

      String activity = ActivityDecisionMap.getActivityDecision(state);
      if (activity == null) {
        log.warn("No next activity found for this state '{}'", state);
        return;
      }

      log.info("Activity '{}' found for this state '{}'", activity, state);
      camunda.getRuntimeService()
        .createMessageCorrelation(activity)
        .processInstanceBusinessKey(caseId)
//          .setVariable(//
//            "PAYLOAD_" + event.getPayload().getName().toUpperCase(), //
//            SpinValues.jsonValue(event.getPayload().getScenarios().toString()).create())//
        .correlateWithResult();
    } catch (MismatchingMessageCorrelationException e) {
      log.error(e.getMessage());
    }
  }

  private void correlateAgreementVerifier(String caseId, String state) {
    if (
        state.equals(GuardEventState.VERIFICATION_PERFORMED.getState())
    ) {
      camunda.getRuntimeService()
        .createMessageCorrelation(GuardEventState.WAIT_FOR_VERIFICATION.getState())
        .processInstanceBusinessKey(caseId)
        .correlateWithResult();
      log.info("AgreementVerifier Activity 'WaitForVerification' found for this state '{}'", state);
    }
  }

  private void correlateSafetyAssessment(String caseId, String state) {
    if (
      state.equals(GuardEventState.NO_CONGESTION_DETECTED.getState()) ||
      state.equals(GuardEventState.CONGESTION_DETECTED.getState())
    ) {
      camunda.getRuntimeService()
        .createMessageCorrelation(GuardEventState.WAIT_FOR_ANALYSIS.getState())
        .processInstanceBusinessKey(caseId)
        .correlateWithResult();
      log.info("SafetyAssessment Activity 'WaitForAnalysis' found for this state '{}'", state);
    }
  }
}
