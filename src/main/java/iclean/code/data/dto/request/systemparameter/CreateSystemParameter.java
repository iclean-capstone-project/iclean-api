package iclean.code.data.dto.request.systemparameter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSystemParameter {
    @NotNull(message = "parameterField not null")
    @NotBlank(message = "parameterField not blank")
    private String parameterField;

    @NotNull(message = "parameterValue not null")
    @NotBlank(message = "parameterValue not blank")
    private String parameterValue;
}
