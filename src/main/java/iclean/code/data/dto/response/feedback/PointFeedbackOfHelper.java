package iclean.code.data.dto.response.feedback;

import lombok.Data;

@Data
public class PointFeedbackOfHelper {
    private Double rate;
    private Long numberOfFeedback;

    public PointFeedbackOfHelper(Double rate, Long numberOfFeedback) {
        this.rate = rate;
        this.numberOfFeedback = numberOfFeedback;
    }
}
