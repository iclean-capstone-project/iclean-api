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
public class WalletHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_history_id")
    private int walletHistoryId;

    @Column(name = "transaction_date", nullable = false)
    private Date transactionDate;

    @Column(nullable = false)
    private double balance;

    @Column(nullable = false)
    private String note;

    @Column(name = "createAt", nullable = false)
    private Date create_at;

    @Column(name = "transaction_status", nullable = false)
    private int transactionStatus;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
