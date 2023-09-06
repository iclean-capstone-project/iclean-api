package iclean.gfa23se46.icleanapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    private String username;

    private String password;

    @Column(name = "first_name")
    private String firstName;

    private String avatar;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String email;

    private String gender;

    private Date birthday;

    @Column(name = "create_at")
    private Date createAt;

    @Column(name = "update_at")
    private Date updateAt;

    @Column(name = "is_active")
    private int isActive;

    @Column(name = "is_block")
    private int isBlock;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    @JsonIgnore
    private List<Address> addresses;

    @OneToMany(mappedBy = "manager")
    @JsonIgnoreProperties({"user", "withdrawalRequest"})
    @JsonIgnore
    private List<WithdrawalRequest> managerWithdrawalRequests;

    @OneToMany(mappedBy = "staff")
    @JsonIgnoreProperties("user")
    @JsonIgnore
    private List<WithdrawalRequest> staffWithdrawalRequests;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    @JsonIgnore
    private List<FCMToken> fcmTokens;

    @OneToMany(mappedBy = "sender")
    @JsonIgnoreProperties("user")
    @JsonIgnore
    private List<Message> senders;

    @OneToMany(mappedBy = "receiver")
    @JsonIgnoreProperties("user")
    @JsonIgnore
    private List<Message> receivers;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    @JsonIgnore
    private List<Payment> payments;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    @JsonIgnore
    private List<Announcement> announcements;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    @JsonIgnore
    private List<Follow> userFollows;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    @JsonIgnore
    private List<Follow> staffFollows;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    @JsonIgnore
    private List<Follow> serviceFollows;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    @JsonIgnore
    private List<VoucherUser> voucherUsers;
}
