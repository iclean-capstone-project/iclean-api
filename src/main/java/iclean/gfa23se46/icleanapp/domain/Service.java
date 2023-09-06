package iclean.gfa23se46.icleanapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private int service_id;

    @Column(name = "service_name")
    private String serviceName;

    private String description;

    private String image;

    @Column(name = "create_at")
    private Date createAt;

    @Column(name = "update_time")
    private Date updateTime;

    @OneToMany(mappedBy = "service")
    @JsonIgnoreProperties("service")
    @JsonIgnore
    private List<ServiceStaff> serviceStaffs;
}
