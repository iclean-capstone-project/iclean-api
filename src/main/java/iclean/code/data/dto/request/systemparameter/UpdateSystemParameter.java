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
public class UpdateSystemParameter {
    private Integer parameterId;

    @Column(name = "parameter_value")
    private String parameterValue;

    @NotNull(message = "updateVersion not null")
    @NotBlank(message = "updateVersion not blank")
    private String updateVersion;
}
