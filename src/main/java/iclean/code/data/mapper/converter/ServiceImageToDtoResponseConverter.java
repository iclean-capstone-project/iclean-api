package iclean.code.data.mapper.converter;

import iclean.code.data.domain.ServiceImage;
import iclean.code.data.dto.response.service.GetServiceImagesResponse;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ServiceImageToDtoResponseConverter implements Converter<ServiceImage, GetServiceImagesResponse> {
    @Override
    public GetServiceImagesResponse convert(MappingContext<ServiceImage, GetServiceImagesResponse> context) {
        ServiceImage source = context.getSource();
        GetServiceImagesResponse response = new GetServiceImagesResponse();
        response.setServiceImage(source.getServiceImage());
        return response;
    }
}
