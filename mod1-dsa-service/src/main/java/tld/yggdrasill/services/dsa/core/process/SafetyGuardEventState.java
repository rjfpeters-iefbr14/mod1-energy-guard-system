package tld.yggdrasill.services.dsa.core.process;

public enum SafetyGuardEventState {

  CONGESTION_DETECTED("congestion-detected"),
  NO_CONGESTION_DETECTED("no-congestion-detected");

  private final String state;

  public String getState() {
    return state;
  }

  SafetyGuardEventState(String state) {
    this.state = state;
  }
}
