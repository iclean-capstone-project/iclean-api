package iclean.code.data.domain;

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
public class RejectionReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rejection_reason_id")
    private Integer rejectionReasonId;

    @Column(name = "rejection_content")
    private String rejectionContent;

    @Column(name = "create_at")
    private LocalDateTime createAt = Utils.getLocalDateTimeNow();
}
