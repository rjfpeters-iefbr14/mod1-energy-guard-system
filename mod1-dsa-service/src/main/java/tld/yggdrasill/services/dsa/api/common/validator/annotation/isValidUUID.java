package tld.yggdrasill.services.dsa.api.common.validator.annotation;


import tld.yggdrasill.services.dsa.api.common.validator.UUIDValidator;

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
@Constraint(validatedBy = UUIDValidator.class)
public @interface isValidUUID {
  String message() default "is not a valid UUID format";
  Class<?>[] groups() default { };
  Class<? extends Payload>[] payload() default { };
}
