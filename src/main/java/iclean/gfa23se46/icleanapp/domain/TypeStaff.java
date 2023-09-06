package iclean.gfa23se46.icleanapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class TypeStaff {

    @Id
    private int user_id;

    @OneToMany(mappedBy = "typeStaff")
    @JsonIgnoreProperties("typeStaff")
    @JsonIgnore
    private List<EmployeeInfo> employeeInfos;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "type_id", nullable = false)
    private Type type;

}
