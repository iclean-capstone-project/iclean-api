package iclean.code.data.dto.response.bookingdetail;

import lombok.Data;

@Data
public class GetBookingDetailResponse {
    private Integer bookingDetailId;
    private String bookingCode;
    private String orderDate;
    private Integer serviceId;
    private Integer serviceUnitId;
    private String serviceName;
    private String serviceIcon;
    private String workDate;
    private String note;
    private String workTime;
    private String value;
    private Double equivalent;
    private Double price;
    private String status;
}