package tld.yggdrasill.services.agm.api.common.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class BadProcessDefinitionRequest extends AbstractThrowableProblem {
  private static final URI TYPE
    = URI.create("https://yggdrasill.tld/contingencies/not-found");

  public BadProcessDefinitionRequest() {
    super(
      TYPE,
      "Contingency",
      Status.NOT_FOUND,
      "no contingency found");
  }

  public BadProcessDefinitionRequest(String id) {
    super(
      TYPE,
      "Contingency",
      Status.NOT_FOUND,
      String.format("'%s' not found", id));
  }
}
