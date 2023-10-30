package iclean.code.data.dto.request.feedback;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateFeedbackDto {
    private Double rate;
    private String feedback;
    private LocalDateTime feedbackTime;
}
