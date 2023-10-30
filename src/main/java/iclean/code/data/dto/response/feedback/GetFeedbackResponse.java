package iclean.code.data.dto.response.feedback;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetFeedbackResponse {
    private String fullName;
    private Double rate;
    private String feedback;
    private LocalDateTime feedbackTime;
}
