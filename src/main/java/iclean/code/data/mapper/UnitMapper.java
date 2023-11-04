package iclean.code.data.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UnitMapper {
    private final ModelMapper modelMapper;
    public UnitMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
