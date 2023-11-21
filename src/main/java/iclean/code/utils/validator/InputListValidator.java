package iclean.code.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.regex.Pattern;

public class InputListValidator implements ConstraintValidator<ValidInputList, List<String>> {
    private String value;

    @Override
    public void initialize(ValidInputList constraintAnnotation) {
        this.value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(List<String> inputList, ConstraintValidatorContext context) {
        if (inputList == null || inputList.isEmpty()) {
            return true;
        }

        Pattern compiledPattern = Pattern.compile(value);
        for (String input : inputList) {
            if (!compiledPattern.matcher(input).matches()) {
                return false;
            }
        }

        return true;
    }
}
