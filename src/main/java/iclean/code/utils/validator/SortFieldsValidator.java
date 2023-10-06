package iclean.code.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SortFieldsValidator implements ConstraintValidator<ValidSortFields, List<String>> {

    private Class<?> objectClass;
    @Override
    public void initialize(ValidSortFields constraintAnnotation) {
        objectClass = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(List<String> sortFields, ConstraintValidatorContext context) {
        if (sortFields == null) {
            return true; // Null values are considered valid
        }

        List<String> allowedSortField = Arrays.stream(objectClass.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());

        for (String sortField : sortFields) {
            if (!allowedSortField.contains(sortField)) {
                return false; // Invalid sort field found
            }
        }

        return true;
    }
}

