package iclean.code.data.dto.request.booking;

import lombok.Data;

@Data
public class AcceptRejectBookingRequest {
    private String action;
    private Integer rejectionReasonId;
    private String rejectionReasonDetail;
}
