package iclean.code.service;

import iclean.code.data.dto.request.NotificationRequestDto;

public interface FCMService {
    public void subscribeToTopic();
    public void unsubscribeFromTopic();
    public String sendPnsToTopic(NotificationRequestDto notificationRequestDto);
}