package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(unique = true)
    private String username;

    private String password;

    private LocalDate dateOfBirth;

    @Column(name = "facebook_uid", unique = true)
    private String facebookUid;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "otp_token")
    private String otpToken;

    @Column(name = "expired_token")
    private LocalDateTime expiredToken;

    @Column(name = "is_locked")
    private Boolean isLocked = false;

    @Column(unique = true)
    private String email;

    @Column(name = "role_id")
    private Integer roleId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Role role;

    @OneToMany(mappedBy = "employee")
    @JsonIgnoreProperties("employee")
    @JsonIgnore
    private List<BookingEmployee> bookingEmployeeList;

    @OneToMany(mappedBy = "renter")
    @JsonIgnoreProperties("renter")
    @JsonIgnore
    private List<Booking> renterBookingList;

    @OneToMany(mappedBy = "employee")
    @JsonIgnoreProperties("employee")
    @JsonIgnore
    private List<Booking> employeeBookingList;

    @OneToMany(mappedBy = "manager")
    @JsonIgnoreProperties("manager")
    @JsonIgnore
    private List<Booking> managerBookingList;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    @JsonIgnore
    private List<Notification> notifications;
}
