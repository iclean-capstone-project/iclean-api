package iclean.code.utils.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SortFieldsValidator.class)
public @interface ValidSortFields {
    String message() default "Invalid sort fields";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    Class<?> value();
}

