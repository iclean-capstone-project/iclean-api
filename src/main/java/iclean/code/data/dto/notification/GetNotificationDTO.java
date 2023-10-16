package iclean.code.data.dto.notification;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetNotificationDTO {
    private Integer notificationId;

    private String title;

    private String content;

    private String notificationImgLink;

    private LocalDateTime createAt;

    private Integer status;

    private Integer userId;

}
