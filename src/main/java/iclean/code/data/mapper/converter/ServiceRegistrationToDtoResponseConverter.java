package iclean.code.data.mapper.converter;

import iclean.code.data.domain.ServiceRegistration;
import iclean.code.data.dto.response.serviceregistration.GetServiceOfHelperResponse;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ServiceRegistrationToDtoResponseConverter implements Converter<ServiceRegistration, GetServiceOfHelperResponse> {
    @Override
    public GetServiceOfHelperResponse convert(MappingContext<ServiceRegistration, GetServiceOfHelperResponse> context) {
        ServiceRegistration source = context.getSource();
        GetServiceOfHelperResponse response = new GetServiceOfHelperResponse();
        response.setServiceRegistrationId(source.getServiceRegistrationId());
        response.setServiceName(source.getService().getServiceName());
        response.setServiceIcon(source.getService().getServiceImage());
        response.setCreateAt(source.getCreateAt());
        response.setStatus(source.getServiceHelperStatus());
        return response;
    }
}
