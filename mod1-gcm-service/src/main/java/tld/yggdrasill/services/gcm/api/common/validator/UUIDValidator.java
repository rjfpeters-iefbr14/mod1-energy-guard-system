package tld.yggdrasill.services.gcm.api.common.validator;

import org.springframework.stereotype.Component;
import tld.yggdrasill.services.gcm.api.common.validator.annotation.isValidUUID;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

@Component
public class UUIDValidator implements ConstraintValidator<isValidUUID, String> {

  private final static Pattern UUID_REGEX_PATTERN =
    Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

  public static boolean isValidUUID(String str) {
    if (str == null) {
      return false;
    }
    return UUID_REGEX_PATTERN.matcher(str).matches();
  }

  @Override
  public boolean isValid(String uuid, ConstraintValidatorContext constraintValidatorContext) {
    return isValidUUID(uuid);
  }
}
