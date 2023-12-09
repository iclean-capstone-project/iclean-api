package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iclean.code.data.enumjava.MoneyRequestEnum;
import iclean.code.data.enumjava.MoneyRequestStatusEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class MoneyRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "otp_token")
    private String otpToken;

    @Column(name = "expired_token")
    private LocalDateTime expiredTime;

    @Column(name = "request_status")
    private MoneyRequestStatusEnum requestStatus;

    @Column(name = "process_date")
    private LocalDateTime processDate;

    @Column(name = "request_type")
    private MoneyRequestEnum requestType;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;
}
