package iclean.code.utils.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidDateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {
    String message() default "Invalid date format or date is not within the specified range";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String min() default "now";

    String max() default "now";
}
