package tld.yggdrasill.services.gcm.api.common.validator.annotation;

import tld.yggdrasill.services.gcm.api.common.validator.SampleValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({FIELD, ANNOTATION_TYPE, PARAMETER})
@Constraint(validatedBy = SampleValidator.class)
public @interface Sample {
  String message() default "{sample}";
  Class<?>[] groups() default { };
  Class<? extends Payload>[] payload() default { };
}
