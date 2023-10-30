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
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("report")
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "report_type_id")
    private ReportType reportType;
}
