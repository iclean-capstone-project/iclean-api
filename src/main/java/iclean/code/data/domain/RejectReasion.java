package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class RejectReasion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rj_reason_id")
    private Integer rjReasonId;

    @Column(name = "rj_content")
    private String rjContent;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @OneToMany(mappedBy = "rejectReasion")
    @JsonIgnoreProperties("rejectReasion")
    @JsonIgnore
    private List<Booking> bookingList;
}
