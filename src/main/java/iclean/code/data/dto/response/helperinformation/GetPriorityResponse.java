package iclean.code.data.dto.response.helperinformation;

import lombok.Data;

@Data
public class GetPriorityResponse {
    private Long numberOfBookingDetail;
    private Double avgRate;
    private Integer helperInformationId;

    public GetPriorityResponse(Long numberOfBookingDetail, Double avgRate, Integer helperInformationId) {
        this.numberOfBookingDetail = numberOfBookingDetail;
        this.avgRate = avgRate;
        this.helperInformationId = helperInformationId;
    }
}
