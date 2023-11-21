package iclean.code.data.mapper;

import iclean.code.data.domain.MoneyRequest;
import iclean.code.data.domain.Notification;
import iclean.code.data.dto.request.moneyrequest.CreateMoneyRequestRequest;
import iclean.code.data.dto.response.notification.GetNotificationResponse;
import iclean.code.utils.Utils;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {
    private final ModelMapper modelMapper;
    public NotificationMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        modelMapper.addMappings(new PropertyMap<Notification, GetNotificationResponse>() {
            @Override
            protected void configure() {
                map().setDetail(source.getContent());
            }
        });
    }

    public <S, D> D map(S source, Class<D> destinationType) {
        return modelMapper.map(source, destinationType);
    }
}
