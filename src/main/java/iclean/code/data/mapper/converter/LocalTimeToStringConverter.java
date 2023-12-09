package iclean.code.data.mapper.converter;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeToStringConverter implements Converter<LocalTime, String> {
    @Override
    public String convert(MappingContext<LocalTime, String> context) {
        LocalTime sourceValue = context.getSource();
        if (sourceValue == null) {
            return null;
        }
        return sourceValue.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
