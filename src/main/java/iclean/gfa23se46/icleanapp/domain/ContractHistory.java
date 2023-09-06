package iclean.gfa23se46.icleanapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class ContractHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private int historyId;

    @Column(name = "change_history_file")
    private String changeHistoryFile;

    @Column(name = "change_date")
    private Date changeDate;

    @Column(name = "change_history")
    private String changeHistory;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "contract_id",insertable = false, updatable = false)
    private Contract contract;
}
