package iclean.code.data.dto.request.notification;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetNotificationDTO {
    private Integer notificationId;

    private String title;

    private String content;

    private String notificationImgLink;

    private LocalDateTime createAt;

    private Boolean isRead;

    private Boolean isEmployee;
}
