package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iclean.code.data.enumjava.TransactionStatusEnum;
import iclean.code.data.enumjava.TransactionTypeEnum;
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
    private TransactionStatusEnum transactionStatusEnum;

    @Column(name = "transaction_type")
    private TransactionTypeEnum transactionTypeEnum;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "booking_id")
    private Booking booking;
}
