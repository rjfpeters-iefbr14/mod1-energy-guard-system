package tld.yggdrasill.services.agm.core.model;

public enum SafetyGuardEventState {

  SAFETY_ASSESSMENT("safety-assessment"),
  SOLUTION_DISCOVERY("solution-discovery"),
  SOLUTION_INQUIRE_MARKET("solution-inquire-market"),
  SOLUTION_ACTIVATION("solution-activation"),
  CONGESTION_RESOLVED("congestion-resolved"),
  CONGESTION_DETECTED("congestion-detected"),
  NO_CONGESTION_DETECTED("no-congestion-detected"),

  WAIT_FOR_ANALYSIS("WaitForAnalysis");


  private final String state;

  public String getState() {
    return state;
  }

  SafetyGuardEventState(String state) {
    this.state = state;
  }
}
