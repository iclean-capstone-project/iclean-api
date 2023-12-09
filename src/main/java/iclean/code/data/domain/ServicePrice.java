package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iclean.code.data.enumjava.ServicePriceEnum;
import iclean.code.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class ServicePrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_price_id")
    private Integer servicePriceId;

    @Column(name = "time_id")
    private ServicePriceEnum servicePriceEnum;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "price")
    private Double price;

    @Column(name = "employee_commission")
    private Double employeeCommission;

    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Column(name = "create_at")
    private LocalDateTime createAt = Utils.getLocalDateTimeNow();

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "service_unit_id")
    private ServiceUnit serviceUnit;
}
