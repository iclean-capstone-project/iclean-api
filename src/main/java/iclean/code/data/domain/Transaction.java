package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iclean.code.data.enumjava.TransactionStatus;
import iclean.code.data.enumjava.TransactionType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    private Double amount;

    private String note;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "transaction_status")
    private TransactionStatus transactionStatus;

    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
}
