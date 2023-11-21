package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iclean.code.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class ServiceUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_unit_id")
    private Integer serviceUnitId;

    @Column(name = "default_price")
    private Double defaultPrice;

    @Column(name = "helper_commission")
    private Double helperCommission;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "create_at")
    private LocalDateTime createAt = Utils.getLocalDateTimeNow();

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "service_id")
    private Service service;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "unit_id")
    private Unit unit;

}
