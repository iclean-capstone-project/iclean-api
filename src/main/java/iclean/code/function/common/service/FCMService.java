package iclean.code.function.common.service;

import iclean.code.data.dto.request.authen.NotificationRequestDto;

public interface FCMService {
    public void subscribeToTopic();
    public void unsubscribeFromTopic();
    public String sendPnsToTopic(NotificationRequestDto notificationRequestDto);
}