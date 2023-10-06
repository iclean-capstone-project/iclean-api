package iclean.code.data.dto.request.booking;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.annotation.RegEx;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddBookingRequest {

    @NotNull(message = "Not valid")
    @Min(value = 3)
    private Double longitude;

    @NotNull(message = "Vĩ độ không được để trống")
    private Double latitude;

    @NotNull(message = "Vị trí không được để trống")
    @NotBlank(message = "Vị trí không được để trống")
    private String location;

    @Length(max = 200, message = "Tối đa 200 từ")
    private String locationDescription;

    @Range(min = 1, max = 8, message = "Giờ làm không được lớn hơn 8 tiếng")
    @NotNull(message = "Vị trí không được để trống")
    private Double workHour;

    @Range(min = 1, max = 8, message = "Giờ làm thực tế không được lớn hơn 8 tiếng")
    @NotNull(message = "Vị trí không được để trống")
    private Double workHourActual;

    @Range(min = 1000, message = "Giá tiền phải lớn hơn 1000 VNĐ")
    @NotNull(message = "Vị trí không được để trống")
    @RegEx
    private Double totalPrice;

    @Range(min = 1, message = "Mã nhân viên phải lớn hơn 1")
    private Integer staffId;

    @Range(min = 1, message = "Mã khách hàng phải lớn hơn 1")
    private Integer renterId;

    @Range(min = 1, message = "Mã công việc phải lớn hơn 1")
    private Integer jobId;

}
