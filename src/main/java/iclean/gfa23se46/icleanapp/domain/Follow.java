package iclean.gfa23se46.icleanapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Follow {
    @Id
    @Column(name = "user_id")
    private int userId;

    @Id
    @Column(name = "service_id")
    private int serviceId;

    @Id
    @Column(name = "staff_id")
    private int staffId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "staff_id", nullable = false, insertable = false, updatable = false)
    private EmployeeInfo staff;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "role_id",insertable = false, updatable = false)
    private ServiceStaff serviceStaff;
}
