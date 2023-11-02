package iclean.code.data.mapper;

import iclean.code.data.domain.Service;
import iclean.code.data.dto.response.service.GetServiceDetailForHelperResponse;
import iclean.code.data.mapper.converter.ServiceImageToDtoResponseConverter;
import iclean.code.data.mapper.converter.ServiceUnitToDtoResponseConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {
    private final ModelMapper modelMapper;
    public ServiceMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.addConverter(new ServiceUnitToDtoResponseConverter());
        modelMapper.addConverter(new ServiceImageToDtoResponseConverter());
        modelMapper.typeMap(Service.class, GetServiceDetailForHelperResponse.class)
                .addMappings(mapper -> {
                    mapper.map(Service::getServiceUnits, GetServiceDetailForHelperResponse::setDetails);
                });
        modelMapper.addMappings(new PropertyMap<Service, GetServiceDetailForHelperResponse>() {
            @Override
            protected void configure() {
                map().setServiceImage(source.getServiceImage());
            }
        });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
