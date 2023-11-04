package iclean.code.data.mapper.converter;

import iclean.code.data.domain.BookingStatusHistory;
import iclean.code.data.dto.response.bookingstatushistory.GetBookingStatusHistoryResponse;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class BookingStatusHistoryToDtoResponseConverter implements Converter<BookingStatusHistory, GetBookingStatusHistoryResponse> {
    @Override
    public GetBookingStatusHistoryResponse convert(MappingContext<BookingStatusHistory, GetBookingStatusHistoryResponse> context) {
        BookingStatusHistory source = context.getSource();
        GetBookingStatusHistoryResponse response = new GetBookingStatusHistoryResponse();
        response.setCreateAt(source.getCreateAt());
        response.setStatusHistoryId(source.getStatusHistoryId());
        response.setBookingStatus(source.getBookingStatus().getValue());
        return response;
    }
}
