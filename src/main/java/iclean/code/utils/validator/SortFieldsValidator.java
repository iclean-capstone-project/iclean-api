package iclean.code.utils.validator;

import iclean.code.utils.Utils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.ArrayList;
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

        List<String> fieldList = new ArrayList<>();
        List<String> directionList = new ArrayList<>();

        for (String item : sortFields) {
            String[] parts = Utils.removeSpace(item).split("_");
            if (parts.length == 2) {
                fieldList.add(parts[0]);
                directionList.add(parts[1]);
            } else if (parts.length == 1) {
                fieldList.add(parts[0]);
                directionList.add("");
            }
        }

        boolean isValid = directionList.stream().allMatch(s -> s.isEmpty() || "asc".equalsIgnoreCase(s) || "desc".equalsIgnoreCase(s));
        if (!isValid)
            return false;

        List<String> allowedSortField = Arrays.stream(objectClass.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());

        for (String sortField : fieldList) {
            if (!allowedSortField.contains(sortField)) {
                return false; // Invalid sort field found
            }
        }

        return true;
    }
}

