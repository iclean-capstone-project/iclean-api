package iclean.code.data.mapper;

import iclean.code.data.domain.HelperInformation;
import iclean.code.data.dto.response.helperinformation.GetHelperInformationDetailResponse;
import iclean.code.data.mapper.converter.ServiceRegistrationToDtoResponseConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class HelperInformationMapper {
    private final ModelMapper modelMapper;

    public HelperInformationMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        modelMapper.addConverter(new ServiceRegistrationToDtoResponseConverter());

        modelMapper.typeMap(HelperInformation.class, GetHelperInformationDetailResponse.class)
                .addMappings(mapper -> {
                    mapper.map(HelperInformation::getServiceRegistrations, GetHelperInformationDetailResponse::setServices);
                });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
