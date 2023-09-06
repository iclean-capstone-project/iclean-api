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
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private int voucherId;

    @Column(name = "voucher_name", nullable = false)
    private String voucherName;

    @Column(name = "voucher_code", nullable = false)
    private String voucherCode;

    @Column(name = "max_price_discount", nullable = false)
    private double maxPriceDiscount;

    @Column(name = "discount_percent", nullable = false)
    private double discountPercent;

    @Column(name = "create_at", nullable = false)
    private Date createAt;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "is_active", nullable = false)
    private int isActive;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "img_voucher", nullable = false)
    private String imgVoucher;

    @OneToMany(mappedBy="voucher")
    @JsonIgnoreProperties("voucher")
    @JsonIgnore
    private List<VoucherUser> voucherUsers;
}
