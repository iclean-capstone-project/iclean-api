package iclean.code.data.mapper.converter;

import iclean.code.data.domain.BookingDetail;
import iclean.code.data.dto.response.bookingdetail.GetCartBookingDetailResponse;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.time.format.DateTimeFormatter;

public class BookingDetailToCartResponseConverter implements Converter<BookingDetail, GetCartBookingDetailResponse> {
    @Override
    public GetCartBookingDetailResponse convert(MappingContext<BookingDetail, GetCartBookingDetailResponse> context) {
        BookingDetail source = context.getSource();
        GetCartBookingDetailResponse response = new GetCartBookingDetailResponse();
        response.setCartItemId(source.getBookingDetailId());
        response.setServiceName(source.getServiceUnit().getService().getServiceName());
        response.setServiceIcon(source.getServiceUnit().getService().getServiceImage());
        response.setWorkDate(source.getWorkDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        response.setWorkTime(source.getWorkStart().format(DateTimeFormatter.ofPattern("HH:mm")));
        response.setServiceId(source.getServiceUnit().getService().getServiceId());
        response.setServiceUnitId(source.getServiceUnit().getServiceUnitId());
        response.setValue(source.getServiceUnit().getUnit().getUnitDetail());
        response.setEquivalent(source.getServiceUnit().getUnit().getUnitValue());
        response.setPrice(source.getPriceDetail());
        return response;
    }
}