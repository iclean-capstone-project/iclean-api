package iclean.code.data.dto.response.booking;

import lombok.Data;

@Data
public class GetBookingDetailAfterCartResponse {
    private Integer detailId;
    private String serviceName;
    private String serviceIcon;
    private String workDate;
    private String note;
    private String workTime;
    private String unitValue;
    private Double price;
    private String helperName;
    private String helperAvatar;
    private String bookingDetailStatus;
    private String helperPhoneNumber;
}
