package iclean.code.data.mapper;

import iclean.code.data.domain.Service;
import iclean.code.data.domain.User;
import iclean.code.data.dto.response.moneyrequest.GetMoneyRequestUserDto;
import iclean.code.data.dto.response.service.GetServiceDetailForHelperResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {
    private final ModelMapper modelMapper;
    public ServiceMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.addConverter(new ServiceUnitToDtoResponseConverter());
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
