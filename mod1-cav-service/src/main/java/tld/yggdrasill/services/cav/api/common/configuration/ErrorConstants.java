package tld.yggdrasill.services.cav.api.common.configuration;

import lombok.NoArgsConstructor;

import java.net.URI;

@NoArgsConstructor
public final class ErrorConstants {
  public static final String ERR_VALIDATION = "error.validation";
  public static final String PROBLEM_BASE_URL = "https://yggdrasill.tld/api/problem";
  public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
  public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
}
