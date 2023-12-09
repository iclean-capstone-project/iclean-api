package iclean.code.data.dto.request.booking;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class AcceptRejectBookingRequest {
    @Pattern(regexp = "(?i)(REJECTED|APPROVED)", message = "Action is not valid")
    @Schema(example = "rejected|approved")
    @NotNull(message = "Action is required")
    @NotBlank(message = "Action cannot be empty")
    private String action;
    private Integer rejectionReasonId;
    private String rejectionReasonDetail;
}
