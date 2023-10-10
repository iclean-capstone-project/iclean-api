package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class JobUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_unit_history_id")
    private Integer jobUnitHistoryId;

    @Column(name = "price_default")
    private Double priceDefault;

    @Column(name = "employee_commission")
    private Double employeeCommission;

    @Column(name = "unit_value")
    private String unitValue;

    @Column(name = "is_delete")
    private Boolean isDelete;

    @Column(name = "img_job_unit")
    private String imgJobUnit;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "job_id", insertable = false, updatable = false)
    private Integer jobId;

    @OneToMany(mappedBy = "jobUnit")
    @JsonIgnoreProperties("jobUnit")
    @JsonIgnore
    private List<Booking> bookings;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "job_id")
    private Job job;
}
