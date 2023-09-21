package iclean.code.data.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
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
}
