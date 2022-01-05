package tld.yggdrasill.services.gcm.api.common.validator;

import org.springframework.stereotype.Component;
import tld.yggdrasill.services.gcm.api.common.validator.annotation.Sample;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class SampleValidator implements ConstraintValidator<Sample, Integer> {

  @Override
  public boolean isValid(Integer age, ConstraintValidatorContext constraintValidatorContext) {
    return true;
  }
}
