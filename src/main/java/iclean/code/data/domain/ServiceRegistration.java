package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iclean.code.data.enumjava.ServiceHelperStatusEnum;
import iclean.code.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class ServiceRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_registration_id")
    private Integer serviceRegistrationId;

    @Column(name = "service_helper_status")
    private ServiceHelperStatusEnum serviceHelperStatus;

    @Column(name = "create_at")
    private LocalDateTime createAt = Utils.getLocalDateTimeNow();

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "helper_information_id")
    private HelperInformation helperInformation;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "service_id")
    private Service service;
}
