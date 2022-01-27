package tld.yggdrasill.services.agm.core.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ActivityDecisionMapTest {

  @Test
  void successful_from_congestion_detected() {
    String activity = ActivityDecisionMap.getActivityDecision("congestion-detected");
    assertThat(activity).isNotBlank();
  }

  @Test
  void successful_from_no_congestion_detected() {
    String activity = ActivityDecisionMap.getActivityDecision("no-congestion-detected");
    assertThat(activity).isNotBlank();
  }
}
