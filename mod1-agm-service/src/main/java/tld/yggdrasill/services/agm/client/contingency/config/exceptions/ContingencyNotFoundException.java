package tld.yggdrasill.services.agm.client.contingency.config.exceptions;

import org.zalando.problem.Problem;

public class ContingencyNotFoundException extends Exception {

  public ContingencyNotFoundException(String message) {super(message);}

  @Override
  public String toString() {
    return String.format("NotFoundException: %s", getMessage());
  }
}
