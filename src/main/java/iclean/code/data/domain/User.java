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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    private String username;

    private String password;

    @Column(name = "facebook_uid")
    private String facebookUid;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "otp_token")
    private String otpToken;
    @Column(name = "is_locked")
    private Boolean isLocked = false;

    private String email;

    @Column(name = "role_id")
    private Integer roleId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Role role;

    @OneToMany(mappedBy = "renter")
    @JsonIgnoreProperties("renter")
    @JsonIgnore
    private List<Booking> renterBookingList;

    @OneToMany(mappedBy = "staff")
    @JsonIgnoreProperties("staff")
    @JsonIgnore
    private List<Booking> staffBookingList;

    @OneToMany(mappedBy = "manager")
    @JsonIgnoreProperties("manager")
    @JsonIgnore
    private List<Booking> managerBookingList;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    @JsonIgnore
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    @JsonIgnore
    private List<MoneyRequest> moneyRequests;

    @OneToOne(mappedBy = "user")
    @JsonIgnoreProperties("user")
    @JsonIgnore
    private MoneyPoint moneyPoints;
}
