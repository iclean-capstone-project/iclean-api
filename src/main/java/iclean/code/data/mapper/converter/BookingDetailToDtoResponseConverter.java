package iclean.code.data.mapper.converter;

import iclean.code.data.domain.BookingDetail;
import iclean.code.data.dto.response.bookingdetail.GetBookingDetailResponse;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.time.format.DateTimeFormatter;

public class BookingDetailToDtoResponseConverter implements Converter<BookingDetail, GetBookingDetailResponse> {
    @Override
    public GetBookingDetailResponse convert(MappingContext<BookingDetail, GetBookingDetailResponse> context) {
        BookingDetail source = context.getSource();
        GetBookingDetailResponse response = new GetBookingDetailResponse();
        response.setBookingDetailId(source.getBookingDetailId());
        response.setServiceName(source.getServiceUnit().getService().getServiceName());
        response.setServiceIcon(source.getServiceUnit().getService().getServiceImage());
        if (source.getWorkStart() != null && source.getWorkDate() != null) {
            response.setWorkDate(source.getWorkDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            response.setWorkTime(source.getWorkStart().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        response.setServiceId(source.getServiceUnit().getService().getServiceId());
        response.setServiceUnitId(source.getServiceUnit().getServiceUnitId());
        response.setValue(source.getServiceUnit().getUnit().getUnitDetail());
        response.setEquivalent(source.getServiceUnit().getUnit().getUnitValue());
        response.setPrice(source.getPriceDetail());
        response.setStatus(source.getBookingDetailStatus().name());
        return response;
    }
}