package iclean.code.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BooleanValidator implements
        ConstraintValidator<BooleanConstraint, Boolean> {

    @Override
    public void initialize(BooleanConstraint contactNumber) {
    }

    @Override
    public boolean isValid(Boolean contactField,
                           ConstraintValidatorContext cxt) {
        if (contactField == null) return false;
        try {
            Boolean.parseBoolean(String.valueOf(contactField));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}