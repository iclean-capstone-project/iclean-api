package iclean.code.data.mapper;

import iclean.code.data.domain.Booking;
import iclean.code.data.dto.response.booking.GetBookingHistoryResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    private final ModelMapper modelMapper;

    public BookingMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.addMappings(new PropertyMap<Booking, GetBookingHistoryResponse>() {
            @Override
            protected void configure() {
//                map().setEmployeeFullName(source.getEmployee().getFullName());
                map().setRenterFullName(source.getRenter().getFullName());
            }
        });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
