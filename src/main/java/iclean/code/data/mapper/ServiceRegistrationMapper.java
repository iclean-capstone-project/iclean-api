package iclean.code.data.mapper;

import iclean.code.data.domain.ServiceRegistration;
import iclean.code.data.dto.response.serviceregistration.GetServiceOfHelperResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class ServiceRegistrationMapper {
    private final ModelMapper modelMapper;
    public ServiceRegistrationMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.addMappings(new PropertyMap<ServiceRegistration, GetServiceOfHelperResponse>() {
            @Override
            protected void configure() {
                map().setServiceIcon(source.getService().getServiceImage());
                map().setServiceName(source.getService().getServiceName());
                map().setServiceRegistrationId(source.getServiceRegistrationId());
                map().setCreateAt(source.getCreateAt());
            }
        });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
