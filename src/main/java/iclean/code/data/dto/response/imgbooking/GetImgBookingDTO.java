package iclean.code.data.dto.response.imgbooking;

import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
public class GetImgBookingDTO {
    private Integer imgBookingId;

    private String imgBookingLink;

    private LocalDateTime createAt;

    private String titleImgType;
}
