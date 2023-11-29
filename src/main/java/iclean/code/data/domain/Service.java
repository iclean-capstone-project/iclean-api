package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "service_name")
    private String serviceName;

    @Lob
    @Column(name = "description", length = 100000)
    private String description;

    @Column(name = "service_image")
    private String serviceImage;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime  updateAt;

    @OneToMany(mappedBy = "service")
    @JsonIgnoreProperties("service")
    @JsonIgnore
    private List<ServiceUnit> serviceUnits;

    @OneToMany(mappedBy = "service")
    @JsonIgnoreProperties("service")
    @JsonIgnore
    private List<ServiceImage> serviceImages;

}

