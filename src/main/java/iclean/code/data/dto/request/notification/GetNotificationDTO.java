package iclean.code.data.dto.request.notification;

import iclean.code.utils.anotation.SortValue;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetNotificationDTO {
    private Integer notificationId;

    private String title;

    @SortValue(value = "content")
    private String detail;

    private String notificationImgLink;

    private LocalDateTime createAt;

    private Boolean isRead;
}
