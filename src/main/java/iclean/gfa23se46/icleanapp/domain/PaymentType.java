package iclean.gfa23se46.icleanapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class PaymentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_type")
    private int paymentType;

    @Column(nullable = false)
    private String title;

    @Column(name = "img_type", nullable = false)
    private String img_type;

    private String description;

    @Column(name = "create_at")
    private Date create_at;

    @OneToMany(mappedBy = "paymentType")
    @JsonIgnoreProperties("payment")
    @JsonIgnore
    private List<Payment> payments;
}
