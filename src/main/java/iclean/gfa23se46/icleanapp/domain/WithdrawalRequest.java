package iclean.gfa23se46.icleanapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class WithdrawalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private int typeId;

    @Column(name = "request_date", nullable = false)
    private Date requestDate;

    @Column(nullable = false)
    private double balance;

    @Column(name = "request_status", nullable = false)
    private int requestStatus;

    @Column(name = "response_date")
    private Date responseDate;

    @Column(name = "request_info", nullable = false)
    private String requestInfo;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "staff_id", nullable = false)
    private User staff;
}
