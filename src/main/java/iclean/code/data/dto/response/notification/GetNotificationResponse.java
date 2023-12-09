package iclean.code.data.dto.response.notification;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetNotificationResponse {
    private Integer notificationId;

    private String title;

    private String detail;

    private String notificationImgLink;

    private LocalDateTime createAt;

    private Boolean isRead;
}
