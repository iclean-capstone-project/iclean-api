package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class HelperInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "helper_information_id")
    private Integer helperInformationId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "nation_id")
    private String nationId;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "helperInformation")
    @JsonIgnoreProperties("helperInformation")
    @JsonIgnore
    private List<Attachment> attachments;

    @OneToMany(mappedBy = "helperInformation")
    @JsonIgnoreProperties("helperInformation")
    @JsonIgnore
    private List<ServiceRegistration> serviceRegistrations;

}
