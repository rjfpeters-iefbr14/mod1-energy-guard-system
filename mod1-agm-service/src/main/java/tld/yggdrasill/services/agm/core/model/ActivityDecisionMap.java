package tld.yggdrasill.services.agm.core.model;

import java.util.Map;

public class ActivityDecisionMap {

  private static final Map<SafetyGuardEventState, String> ACTIVITY_DECISION_MAP =
    Map.of(
      SafetyGuardEventState.CONGESTION_DETECTED, "CongestionDetected",
      SafetyGuardEventState.NO_CONGESTION_DETECTED, "NoCongestionDetected",
      SafetyGuardEventState.CONGESTION_RESOLVED, "CongestionResolved",
      SafetyGuardEventState.SOLUTION_DISCOVERY, "SolutionDiscovery",
      SafetyGuardEventState.SOLUTION_INQUIRE_MARKET, "InquireMarket",
      SafetyGuardEventState.SOLUTION_ACTIVATION, "SolutionActivation"
    );

  public static String getActivityDecision(String eventState) {
    SafetyGuardEventState state = SafetyGuardEventState.valueOf(
      eventState.replace('-','_').toUpperCase());
    return ACTIVITY_DECISION_MAP.get(state);
  }
}
