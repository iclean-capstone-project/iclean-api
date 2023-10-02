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
public class RejectReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rj_reason_id")
    private Integer rejectReasonId;

    @Column(name = "rj_content")
    private String rj_content;

    @Column(name = "create_at")
    private LocalDateTime createAt;
}
