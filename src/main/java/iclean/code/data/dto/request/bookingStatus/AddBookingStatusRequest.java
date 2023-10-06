package iclean.code.data.dto.request.bookingStatus;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddBookingStatusRequest {

    @NotNull(message = "Tên trạng thái không được trống")
    @NotBlank(message = "Tên trạng thái không được trống")
    @Length(max = 200, message = "Tối đa 200 từ")
    private String titleStatus;
}
