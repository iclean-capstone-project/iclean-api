package iclean.code.data.dto.request.feedback;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FeedbackRequest {
    @Range(min = 1, max = 5, message = "Rate cannot greater than 5 and smaller than 1")
    private Double rate;
    @NotNull(message = "Feedback cannot be null")
    @NotBlank(message = "Feedback cannot be empty")
    private String feedback;
}
