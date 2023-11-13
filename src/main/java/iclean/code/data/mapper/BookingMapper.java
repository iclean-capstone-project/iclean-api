package iclean.code.data.mapper;

import iclean.code.data.domain.Booking;
import iclean.code.data.dto.response.booking.GetDetailBookingResponse;
import iclean.code.data.dto.response.booking.GetBookingHistoryResponse;
import iclean.code.data.dto.response.booking.GetBookingResponse;
import iclean.code.data.dto.response.booking.GetCartResponseDetail;
import iclean.code.data.mapper.converter.BookingDetailToDtoResponseConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    private final ModelMapper modelMapper;

    public BookingMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.addConverter(new BookingDetailToDtoResponseConverter());
        modelMapper.typeMap(Booking.class, GetCartResponseDetail.class)
                .addMappings(mapper -> {
                    mapper.map(Booking::getBookingDetails, GetCartResponseDetail::setDetails);
                });

        modelMapper.typeMap(Booking.class, GetDetailBookingResponse.class)
                .addMappings(mapper -> {
                    mapper.map(Booking::getBookingStatus, GetDetailBookingResponse::setCurrentStatus);
                    mapper.map(Booking::getBookingDetails, GetDetailBookingResponse::setDetails);
                    mapper.map(Booking::getBookingStatusHistories, GetDetailBookingResponse::setStatuses);
                });
        modelMapper.typeMap(Booking.class, GetBookingResponse.class)
                .addMappings(mapper -> {
                    mapper.map(Booking::getBookingDetails, GetBookingResponse::setServiceNames);
                });
        modelMapper.addMappings(new PropertyMap<Booking, GetBookingHistoryResponse>() {
            @Override
            protected void configure() {
                map().setRenterFullName(source.getRenter().getFullName());
            }
        });
        modelMapper.addMappings(new PropertyMap<Booking, GetBookingResponse>() {
            @Override
            protected void configure() {
                map().setBookingId(source.getBookingId());
                map().setBookingCode(source.getBookingCode());
                map().setRenterName(source.getRenter().getFullName());
                map().setRenterAvatar(source.getRenter().getAvatar());
                map().setRenterPhoneNumber(source.getRenter().getPhoneNumber());
                map().setOrderDate(source.getOrderDate());
                map().setRequestCount(source.getRequestCount());
                map().setTotalPrice(source.getTotalPrice());
                map().setTotalPriceActual(source.getTotalPriceActual());
                map().setUpdateAt(source.getUpdateAt());
            }
        });
        modelMapper.addMappings(new PropertyMap<Booking, GetCartResponseDetail>() {
            @Override
            protected void configure() {
                map().setCartId(source.getBookingId());
            }
        });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
