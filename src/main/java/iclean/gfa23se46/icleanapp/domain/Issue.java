package iclean.gfa23se46.icleanapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id")
    private int issueId;

    private String detail;

    private String reason;

    @Column(name = "create_at")
    private Date createAt;

    @Column(name = "update_at")
    private Date updateAt;

    @Column(name = "issue_status")
    private String issueStatus;

    private String solution;

    private double refund;

    @OneToOne(mappedBy = "issue")
    @JsonIgnoreProperties("issue")
    private Booking booking;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "type_issue_id")
    private TypeIssue typeIssue;
}
