package tld.yggdrasill.services.agm.core.model;

import java.util.Map;

public class ActivityDecisionMap {

  private static final Map<GuardEventState, String> ACTIVITY_DECISION_MAP =
    Map.of(
      GuardEventState.CONGESTION_DETECTED, "CongestionDetected",
      GuardEventState.NO_CONGESTION_DETECTED, "NoCongestionDetected",
      GuardEventState.CONGESTION_RESOLVED, "CongestionResolved",
      GuardEventState.SOLUTION_DISCOVERY, "SolutionDiscovery",
      GuardEventState.SOLUTION_INQUIRE_MARKET, "InquireMarket",
      GuardEventState.SOLUTION_ACTIVATION, "SolutionActivation"
    );

  public static String getActivityDecision(String eventState) {
    GuardEventState state = GuardEventState.valueOf(
      eventState.replace('-','_').toUpperCase());
    return ACTIVITY_DECISION_MAP.get(state);
  }
}
