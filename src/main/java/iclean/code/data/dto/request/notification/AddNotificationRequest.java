package iclean.code.data.dto.request.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddNotificationRequest {
    @Length(max = 200, message = "Tối đa 200 từ")
    @NotNull(message = "title không được để trống")
    @NotBlank(message = "title không được để trống")
    private String title;

    @Length(max = 200, message = "Tối đa 200 từ")
    @NotNull(message = "content không được để trống")
    @NotBlank(message = "content không được để trống")
    private String content;

    @Length(max = 200, message = "Tối đa 200 từ")
    @NotNull(message = "notificationImgLink không được để trống")
    @NotBlank(message = "notificationImgLink không được để trống")
    private String notificationImgLink;

    private Integer userId;
}
