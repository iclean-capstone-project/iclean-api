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
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private int contractId;

    private String file;

    @Column(name = "sign_date")
    private Date signDate;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "contract_status")
    private String contractStatus;

    @Column(name = "addition_info")
    private String additionInfo;

    @OneToMany(mappedBy = "contract")
    @JsonIgnoreProperties("contract")
    @JsonIgnore
    private List<EmployeeInfo> employeeInfos;

    @OneToMany(mappedBy = "contract")
    @JsonIgnoreProperties("contract")
    @JsonIgnore
    private List<ContractHistory> contractHistories;
}
