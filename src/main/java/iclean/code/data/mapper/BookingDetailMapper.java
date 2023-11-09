package iclean.code.data.mapper;

import iclean.code.data.domain.BookingDetail;
import iclean.code.data.dto.response.bookingdetailhelper.GetBookingDetailHelperResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class BookingDetailMapper {
    private final ModelMapper modelMapper;
    public BookingDetailMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        modelMapper.addMappings(new PropertyMap<BookingDetail, GetBookingDetailHelperResponse>() {
            @Override
            protected void configure() {
                map().setServiceIcon(source.getServiceUnit().getService().getServiceImage());
                map().setServiceName(source.getServiceUnit().getService().getServiceName());
                map().setWorkDate(source.getWorkDate());
                map().setOrderDate(source.getCreateAt());
                map().setWorkStart(source.getWorkStart());
                map().setTotalPrice(source.getPriceDetail());
            }
        });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
