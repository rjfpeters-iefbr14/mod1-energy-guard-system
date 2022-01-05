package tld.yggdrasill.services.dsa.client.contingency.config.exceptions;

public class NotFoundException extends Exception {

  public NotFoundException(String message) {
    super(message);
  }

  @Override
  public String toString() {
    return String.format("NotFoundException: %s", getMessage());
  }
}
