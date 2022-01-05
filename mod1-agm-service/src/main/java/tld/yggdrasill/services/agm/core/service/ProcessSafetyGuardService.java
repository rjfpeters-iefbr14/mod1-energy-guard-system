package tld.yggdrasill.services.agm.core.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tld.yggdrasill.services.agm.core.model.ActivityDecisionMap;
import tld.yggdrasill.services.agm.core.model.SafetyGuardEventState;
import tld.yggdrasill.services.cgs.model.GridServiceEvent;
import tld.yggdrasill.services.cgs.model.Payload;

import static net.logstash.logback.argument.StructuredArguments.kv;


@Slf4j
@Service
public class ProcessSafetyGuardService {

  private ProcessEngine camunda;

  @Autowired
  public ProcessSafetyGuardService(ProcessEngine camunda) {
    this.camunda = camunda;
  }

  public void processEvent(String caseId, GridServiceEvent event) {
    try {
      log.info("Correlating {} to waiting flow instance", kv("caseId", caseId));

      Payload payload = event.getPayload();
      String state = payload.getState();

      if (state.equals(SafetyGuardEventState.NO_CONGESTION_DETECTED.getState()) || state.equals(
        SafetyGuardEventState.CONGESTION_DETECTED.getState())) {
        camunda.getRuntimeService()
          .createMessageCorrelation(SafetyGuardEventState.WAIT_FOR_ANALYSIS.getState())
          .processInstanceBusinessKey(caseId)
          .correlateWithResult();
        log.info("Activity 'WaitForAnalysis' found for this state '{}'", state);
      }

      String activity = ActivityDecisionMap.getActivityDecision(state);
      if (activity == null) {
        log.warn("No activity found for this state '{}'", state);
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
}
