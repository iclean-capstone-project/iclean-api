package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iclean.code.data.enumjava.GenderEnum;
import iclean.code.data.enumjava.HelperStatusEnum;
import iclean.code.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Column(name = "meeting_date_time")
    private LocalDateTime meetingDateTime;

    @Column(name = "personal_avatar")
    private String personalAvatar;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "mail")
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "nation_id")
    private String nationId;

    @Column(name = "gender")
    private GenderEnum gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "date_of_issue")
    private LocalDate dateOfIssue;

    @Column(name = "date_of_expired")
    private LocalDate dateOfExpired;

    @Column(name = "place_of_issue")
    private String placeOfIssue;

    @Column(name = "place_of_residence")
    private String placeOfResidence;

    @Column(name = "home_town")
    private String homeTown;

    @Column(name = "personal_identification")
    private String personalIdentification;

    @Column(name = "schedule_followed")
    private Boolean scheduleFollowed = Boolean.FALSE;

    @Column(name = "helper_status")
    private HelperStatusEnum helperStatus = HelperStatusEnum.WAITING_FOR_APPROVE;

    @Column(name = "create_at")
    private LocalDateTime createAt = Utils.getLocalDateTimeNow();

    @Column(name = "manager_id")
    private Integer managerId;

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
    private List<WorkSchedule> workSchedules;

    @OneToMany(mappedBy = "helperInformation")
    @JsonIgnoreProperties("helperInformation")
    @JsonIgnore
    private List<ServiceRegistration> serviceRegistrations;

}
