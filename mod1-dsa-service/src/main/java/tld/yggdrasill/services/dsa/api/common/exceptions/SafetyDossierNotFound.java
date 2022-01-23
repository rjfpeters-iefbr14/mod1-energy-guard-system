package tld.yggdrasill.services.dsa.api.common.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class SafetyDossierNotFound extends AbstractThrowableProblem {
  private static final URI TYPE
    = URI.create("https://yggdrasill.tld/safety-dossier/not-found");

  public SafetyDossierNotFound() {
    super(
      TYPE,
      "SafetyDossier",
      Status.NOT_FOUND,
      "no dossier found");
  }

  public SafetyDossierNotFound(String id) {
    super(
      TYPE,
      "SafetyDossier",
      Status.NOT_FOUND,
      String.format("with id '%s'", id));
  }
}
