package tld.yggdrasill.services.gcm.core.utils.exceptions;

public class BasicPredicateBuilderException extends IllegalArgumentException {
  private String parameterName;
  private String actualValue;
  private String expectedValue;

  public BasicPredicateBuilderException(String parameterName, String actualValue, String expectedValue) {
    super("Bad Request. Parameter " + parameterName + " has value: " + actualValue + ". But expected value(s): "
            + expectedValue);
    this.parameterName = parameterName;
    this.actualValue = actualValue;
    this.expectedValue = expectedValue;
  }

  public String getErrorMessage() {
    return "Bad Request. Parameter " + parameterName + " has value: " + actualValue + ". But expected value(s): " + expectedValue;
  }
}
