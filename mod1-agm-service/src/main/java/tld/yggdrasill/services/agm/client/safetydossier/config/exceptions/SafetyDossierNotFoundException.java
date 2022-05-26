package tld.yggdrasill.services.agm.client.safetydossier.config.exceptions;

public class SafetyDossierNotFoundException extends Exception {

  public SafetyDossierNotFoundException(String message) {super(message);}

  @Override
  public String toString() {
    return String.format("NotFoundException: %s", getMessage());
  }
}
