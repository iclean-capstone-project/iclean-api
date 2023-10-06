package iclean.code.data.dto.request.imgbooking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateImgBooking {

    @NotNull(message = "imgBookingLink không được trống")
    @NotBlank(message = "imgBookingLink không được trống")
    @Length(max = 200, message = "Tối đa 200 từ")
    private String imgBookingLink;

    @Range(min = 1, message = "imgTypeId phải lớn hơn 1")
    @NotNull(message = "imgTypeId không được để trống")
    private Integer imgTypeId;
}
