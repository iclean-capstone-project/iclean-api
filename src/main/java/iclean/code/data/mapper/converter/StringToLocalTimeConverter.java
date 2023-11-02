package iclean.code.data.mapper.converter;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class StringToLocalTimeConverter implements Converter<String, LocalTime> {
    @Override
    public LocalTime convert(MappingContext<String, LocalTime> context) {
        String sourceValue = context.getSource();
        if (sourceValue == null) {
            return null;
        }
        return LocalTime.parse(sourceValue, DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}

