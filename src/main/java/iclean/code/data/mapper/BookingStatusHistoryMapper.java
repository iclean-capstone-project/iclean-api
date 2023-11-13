package iclean.code.data.mapper;

import iclean.code.data.domain.BookingStatusHistory;
import iclean.code.data.dto.response.bookingstatushistory.GetBookingStatusHistoryResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class BookingStatusHistoryMapper {
    private final ModelMapper modelMapper;
    public BookingStatusHistoryMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;


        modelMapper.typeMap(BookingStatusHistory.class, GetBookingStatusHistoryResponse.class)
                .addMappings(mapper -> {
                    mapper.map(BookingStatusHistory::getStatusHistoryId, GetBookingStatusHistoryResponse::setStatusHistoryId);
                    mapper.map(BookingStatusHistory::getCreateAt, GetBookingStatusHistoryResponse::setCreateAt);
                    mapper.map(BookingStatusHistory::getBookingStatus, GetBookingStatusHistoryResponse::setBookingStatus);
                });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
