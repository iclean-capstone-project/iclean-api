package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_app_id")
    private Integer jobApplicationId;

    @Column(name = "job_img_link")
    private String jobImgLink;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "register_id")
    private RegisterEmployee registerEmployee;
}
