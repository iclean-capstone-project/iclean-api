package iclean.code.data.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "System_Parameter")
public class SystemParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parameter_id")
    private Integer parameterId;

    @Column(name = "parameter_field", unique = true)
    private String parameterField;

    @Column(name = "parameter_value")
    private String parameterValue;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "update_version")
    private String updateVersion;
}
