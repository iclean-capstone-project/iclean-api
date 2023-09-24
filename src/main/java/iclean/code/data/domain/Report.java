package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private int reportId;

    private String detail;

    private String solution;

    @Column(name = "refund_percent")
    private Double refundPercent;

    @Column(name = "report_status")
    private String reportStatus;

    private Double refund;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "process_at")
    private LocalDateTime processAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties("report")
    @JoinColumn(name = "booking_id")
    private Booking booking;

}
