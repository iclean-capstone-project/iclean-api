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
public class ServiceStaff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int roleId;

    private String title;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "is_active")
    private int isActive;

    @OneToMany(mappedBy = "serviceStaff")
    @JsonIgnoreProperties("serviceStaff")
    @JsonIgnore
    private List<Follow> follows;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "service_id", nullable = false, insertable = false, updatable = false)
    private Service service;
}
