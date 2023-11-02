package iclean.code.data.dto.response.bookingdetail;

import lombok.Data;

@Data
public class GetBookingDetailResponse {
    private Integer detailId;
    private String serviceName;
    private String serviceIcon;
    private String workDate;
    private String workTime;
    private String unitValue;
    private Double price;
}
