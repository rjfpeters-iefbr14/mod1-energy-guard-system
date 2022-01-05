package tld.yggdrasill.services.gcm.api.common.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class ContingencyNotFound extends AbstractThrowableProblem {
  private static final URI TYPE
    = URI.create("https://yggdrasill.tld/contingencies/not-found");

  public ContingencyNotFound() {
    super(
      TYPE,
      "Contingency",
      Status.NOT_FOUND,
      "no contingency found");
  }

  public ContingencyNotFound(String id) {
    super(
      TYPE,
      "Contingency",
      Status.NOT_FOUND,
      String.format("with id '%s'", id));
  }
}
