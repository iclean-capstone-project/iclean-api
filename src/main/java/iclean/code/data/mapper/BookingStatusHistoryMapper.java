package iclean.code.data.mapper;

import iclean.code.data.domain.BookingDetailStatusHistory;
import iclean.code.data.dto.response.bookingstatushistory.GetBookingStatusHistoryResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class BookingStatusHistoryMapper {
    private final ModelMapper modelMapper;
    public BookingStatusHistoryMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.typeMap(BookingDetailStatusHistory.class, GetBookingStatusHistoryResponse.class)
                .addMappings(mapper -> {
                    mapper.map(BookingDetailStatusHistory::getStatusHistoryId, GetBookingStatusHistoryResponse::setStatusHistoryId);
                    mapper.map(BookingDetailStatusHistory::getCreateAt, GetBookingStatusHistoryResponse::setCreateAt);
                    mapper.map(BookingDetailStatusHistory::getBookingDetailStatus, GetBookingStatusHistoryResponse::setBookingDetailStatus);
                });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
