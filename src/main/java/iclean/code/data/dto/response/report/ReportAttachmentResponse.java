package iclean.code.data.dto.response.report;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportAttachmentResponse {
    private Integer bookingAttachmentId;
    private String bookingAttachmentLink;
    private LocalDateTime createAt;
}
