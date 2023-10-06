package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class MoneyPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "money_point_id")
    private Integer moneyPointId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("moneyPoint")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "current_point")
    private Integer currentPoint;

    @Column(name = "current_money")
    private Integer currentMoney;

    @Column(name = "update_at")
    private LocalDateTime updateAt;
}
