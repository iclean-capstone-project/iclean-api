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
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Integer addressId;

    private Double longitude;

    private Double latitude;

    private String description;

    private String street;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt = Utils.getDateTimeNow();

    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", insertable = true, updatable = true)
    private User user;

}
