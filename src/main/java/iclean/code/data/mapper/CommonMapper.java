package iclean.code.data.mapper;

import iclean.code.data.mapper.converter.LocalDateToStringConverter;
import iclean.code.data.mapper.converter.LocalTimeToStringConverter;
import iclean.code.data.mapper.converter.StringToLocalTimeConverter;
import iclean.code.data.mapper.converter.StringToStringConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CommonMapper {
    private final ModelMapper modelMapper;
    public CommonMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.addConverter(new LocalDateToStringConverter());
        modelMapper.addConverter(new StringToStringConverter());
        modelMapper.addConverter(new StringToLocalTimeConverter());
        modelMapper.addConverter(new LocalTimeToStringConverter());
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
