package iclean.code.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ValidDateValidator implements ConstraintValidator<ValidDate, String> {
    private String min;
    private String max;

    @Override
    public void initialize(ValidDate constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(value, formatter);
        LocalDateTime currentDateTime = LocalDateTime.now();
        ZoneId gmtPlus7Zone = ZoneId.of("GMT+7");
        long currentTime = currentDateTime.atZone(gmtPlus7Zone).toInstant().toEpochMilli();
        if (min.equals("now") || dateTime.isBefore(currentDateTime)
                || (currentTime - dateTime.atZone(gmtPlus7Zone).toInstant().toEpochMilli()) >= parseTimeUnit(min)) {
            return true;
        }
        if (max.equals("now") || dateTime.isAfter(currentDateTime)
            || (currentTime - dateTime.atZone(gmtPlus7Zone).toInstant().toEpochMilli()) <= parseTimeUnit(max)) {
            return true;
        }
        return false;
    }

    private long parseTimeUnit(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }

        long multiplier;
        if (value.endsWith("s")) {
            multiplier = 1000;
        } else if (value.endsWith("m")) {
            multiplier = 60 * 1000;
        } else if (value.endsWith("h")) {
            multiplier = 60 * 60 * 1000;
        } else if (value.endsWith("d")) {
            multiplier = 24 * 60 * 60 * 1000;
        } else if (value.endsWith("y")) {
            multiplier = 365L * 24 * 60 * 60 * 1000;
        } else {
            return 0;
        }

        String numericValue = value.substring(0, value.length() - 1);
        try {
            return Long.parseLong(numericValue) * multiplier;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
