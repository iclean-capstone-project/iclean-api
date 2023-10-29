package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class ReportType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_type_id")
    private int reportTypeId;

    @Column(name= "report_name")
    private String reportName;

    @OneToMany(mappedBy = "reportType")
    @JsonIgnoreProperties("reportType")
    @JsonIgnore
    private List<Report> reportList;
}
