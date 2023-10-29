package iclean.code.data.mapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateToStringConverter implements Converter<LocalDate, String> {
    @Override
    public String convert(MappingContext<LocalDate, String> context) {
        LocalDate sourceValue = context.getSource();
        if (sourceValue == null) {
            return null;
        }
        return sourceValue.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
