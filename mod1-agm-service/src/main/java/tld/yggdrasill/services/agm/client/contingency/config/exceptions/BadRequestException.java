package tld.yggdrasill.services.agm.client.contingency.config.exceptions;

public class BadRequestException extends Exception {

  public BadRequestException(String message) {
    super(message);
  }

  @Override
  public String toString() {
    return String.format("BadRequestException: %s", getMessage());
  }
}
