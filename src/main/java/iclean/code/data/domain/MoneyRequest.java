package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Column(name = "request_status")
    private String requestStatus;

    @Column(name = "process_date")
    private LocalDateTime processDate;

    @Column(name = "is_withdrawal")
    private Boolean isWithDrawl = false;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private User user;
}
