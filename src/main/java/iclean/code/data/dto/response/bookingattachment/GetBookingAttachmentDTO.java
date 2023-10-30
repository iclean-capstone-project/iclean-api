package iclean.code.data.dto.response.bookingattachment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetBookingAttachmentDTO {
    private Integer bookingAttachmentId;

    private String bookingAttachmentLink;

    private LocalDateTime createAt;

    private String titleAttachmentType;
}
