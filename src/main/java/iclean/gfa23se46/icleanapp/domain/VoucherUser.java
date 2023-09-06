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
public class VoucherUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private int voucherId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "booking_id")
    private int bookingId;

    @Column(name = "using_date")
    private Date usingDate;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "voucher_id", nullable = false, insertable = false, updatable = false)
    private Voucher voucher;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "booking_id", nullable = false, insertable = false, updatable = false)
    private Booking booking;
}
