package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iclean.code.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private Integer unitId;

    @Column(name = "unit_detail")
    private String unitDetail;

    @Column(name = "unit_value")
    private String unitValue;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "create_at")
    private LocalDateTime createAt = Utils.getDateTimeNow();

    @OneToMany(mappedBy = "unit")
    @JsonIgnoreProperties("unit")
    @JsonIgnore
    private List<ServiceUnit> serviceUnits;
}
