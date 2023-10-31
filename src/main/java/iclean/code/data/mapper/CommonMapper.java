package iclean.code.data.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CommonMapper {
    private final ModelMapper modelMapper;
    public CommonMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.addConverter(new LocalDateToStringConverter());
        modelMapper.addConverter(new StringToStringConverter());
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
