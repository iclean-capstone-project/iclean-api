package iclean.code.data.mapper;

import iclean.code.data.domain.BookingDetail;
import iclean.code.data.dto.response.feedback.GetFeedbackResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {
    private final ModelMapper modelMapper;

    public FeedbackMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.addMappings(new PropertyMap<BookingDetail, GetFeedbackResponse>() {
            @Override
            protected void configure() {
                map().setFullName(source.getBooking().getRenter().getFullName());
            }
        });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
