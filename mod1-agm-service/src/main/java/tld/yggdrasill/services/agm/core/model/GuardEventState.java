package tld.yggdrasill.services.agm.core.model;

public enum GuardEventState {

  SAFETY_ASSESSMENT("safety-assessment"),

  SOLUTION_DISCOVERY("solution-discovery"),
  SOLUTION_INQUIRE_MARKET("solution-inquire-market"),
  SOLUTION_ACTIVATION("solution-activation"),

  CONGESTION_RESOLVED("congestion-resolved"),
  CONGESTION_DETECTED("congestion-detected"),
  CONTINGENCY_VERIFIER("contingency-verifier"),
  CONTINGENCY_VERIFIED("contingency-verified"),

  NO_CONGESTION_DETECTED("no-congestion-detected"),

  VERIFICATION_PERFORMED("verification-performed"),

  WAIT_FOR_VERIFICATION("WaitForVerification"),
  WAIT_FOR_ANALYSIS("WaitForAnalysis");


  private final String state;

  public String getState() {
    return state;
  }

  GuardEventState(String state) {
    this.state = state;
  }
}
