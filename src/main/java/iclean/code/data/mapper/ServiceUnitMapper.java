package iclean.code.data.mapper;

import iclean.code.data.domain.ServiceUnit;
import iclean.code.data.dto.response.serviceunit.GetServiceUnitResponseForRenter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class ServiceUnitMapper {
    private final ModelMapper modelMapper;
    public ServiceUnitMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
