package iclean.code.data.mapper;

import iclean.code.data.domain.Booking;
import iclean.code.data.dto.response.booking.GetDetailBookingResponse;
import iclean.code.data.dto.response.booking.GetBookingHistoryResponse;
import iclean.code.data.dto.response.booking.GetBookingResponse;
import iclean.code.data.dto.response.booking.GetCartResponseDetail;
import iclean.code.data.dto.response.bookingdetail.GetAddressResponseBooking;
import iclean.code.data.dto.response.bookingdetail.GetCheckOutResponseDetail;
import iclean.code.data.dto.response.bookingdetail.GetRequestBookingDetailAsDto;
import iclean.code.data.mapper.converter.BookingDetailToCartResponseConverter;
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
        modelMapper.addConverter(new BookingDetailToCartResponseConverter());
        modelMapper.typeMap(Booking.class, GetCartResponseDetail.class)
                .addMappings(mapper -> {
                    mapper.map(Booking::getBookingDetails, GetCartResponseDetail::setDetails);
                });

        modelMapper.typeMap(Booking.class, GetCheckOutResponseDetail.class)
                .addMappings(mapper -> {
                    mapper.map(Booking::getBookingDetails, GetCheckOutResponseDetail::setDetails);
                });

        modelMapper.typeMap(Booking.class, GetRequestBookingDetailAsDto.class)
                .addMappings(mapper -> {
                    mapper.map(Booking::getBookingDetails, GetRequestBookingDetailAsDto::setDetails);
                });

        modelMapper.typeMap(Booking.class, GetDetailBookingResponse.class)
                .addMappings(mapper -> {
                    mapper.map(Booking::getBookingStatus, GetDetailBookingResponse::setCurrentStatus);
                    mapper.map(Booking::getBookingDetails, GetDetailBookingResponse::setDetails);
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
                map().setManagerName(source.getManager().getFullName());
            }
        });
        modelMapper.addMappings(new PropertyMap<Booking, GetDetailBookingResponse>() {
            @Override
            protected void configure() {
                map().setRejectionReasonDescription(source.getRjReasonDescription());
                map().setRejectionReasonContent(source.getRejectionReason().getRejectionContent());
            }
        });
        modelMapper.addMappings(new PropertyMap<Booking, GetCartResponseDetail>() {
            @Override
            protected void configure() {
                map().setCartId(source.getBookingId());
            }
        });

        modelMapper.addMappings(new PropertyMap<Booking, GetCheckOutResponseDetail>() {
            @Override
            protected void configure() {
                map().setCartId(source.getBookingId());
                map().setLongitude(source.getLongitude());
                map().setLatitude(source.getLatitude());
                map().setLocationName(source.getLocation());
                map().setLocationDescription(source.getLocationDescription());
                map().setTotalPrice(source.getTotalPrice());
                map().setTotalPriceActual(source.getTotalPriceActual());
                map().setUsingPoint(source.getUsingPoint());
            }
        });

        modelMapper.addMappings(new PropertyMap<Booking, GetRequestBookingDetailAsDto>() {
            @Override
            protected void configure() {
                map().setCartId(source.getBookingId());
                map().setLongitude(source.getLongitude());
                map().setLatitude(source.getLatitude());
                map().setLocationName(source.getLocation());
                map().setLocationDescription(source.getLocationDescription());
                map().setTotalPrice(source.getTotalPrice());
                map().setTotalPriceActual(source.getTotalPriceActual());
                map().setUsingPoint(source.getUsingPoint());
            }
        });

        modelMapper.addMappings(new PropertyMap<Booking, GetAddressResponseBooking>() {
            @Override
            protected void configure() {
                map().setLongitude(source.getLongitude());
                map().setLatitude(source.getLatitude());
                map().setLocationName(source.getLocation());
                map().setLocationDescription(source.getLocationDescription());
            }
        });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
