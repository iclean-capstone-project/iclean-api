package iclean.code.data.mapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class StringToStringConverter implements Converter<String, String> {
    @Override
    public String convert(MappingContext<String, String> context) {
        String sourceValue = context.getSource();
        if (sourceValue == null) {
            return null;
        }
        return sourceValue.replace("  ", " ");
    }
}
