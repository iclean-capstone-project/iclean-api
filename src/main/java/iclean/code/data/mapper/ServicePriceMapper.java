package iclean.code.data.mapper;

import iclean.code.data.domain.ServicePrice;
import iclean.code.data.dto.response.serviceprice.GetServicePriceResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class ServicePriceMapper {
    private final ModelMapper modelMapper;
    public ServicePriceMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        modelMapper.addMappings(new PropertyMap<ServicePrice, GetServicePriceResponse>() {
            @Override
            protected void configure() {
                map().setPrice(source.getPrice());
                map().setEmployeeCommission(source.getEmployeeCommission());
            }
        });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}