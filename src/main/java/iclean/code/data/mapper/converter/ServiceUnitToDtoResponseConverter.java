package iclean.code.data.mapper.converter;

import iclean.code.data.domain.ServiceUnit;
import iclean.code.data.domain.Unit;
import iclean.code.data.dto.response.serviceunit.GetServiceUnitResponseForHelper;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ServiceUnitToDtoResponseConverter implements Converter<ServiceUnit, GetServiceUnitResponseForHelper> {
    @Override
    public GetServiceUnitResponseForHelper convert(MappingContext<ServiceUnit, GetServiceUnitResponseForHelper> context) {
        ServiceUnit source = context.getSource();
        Unit unit = source.getUnit();
        GetServiceUnitResponseForHelper response = new GetServiceUnitResponseForHelper();
        response.setServiceUnitId(source.getServiceUnitId());
        response.setServiceUnitImage(unit.getUnitImage());
        response.setValueUnit(String.format("%s ~ %s giờ", unit.getUnitDetail(), unit.getUnitValue()));
        response.setHelperCommissionPrice(source.getHelperCommission() * source.getDefaultPrice() / 100);
        return response;
    }
}
