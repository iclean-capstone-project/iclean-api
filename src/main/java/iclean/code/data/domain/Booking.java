package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iclean.code.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingId;

    private String location;

    @Column(name = "location_description")
    private String locationDescription;

    private Double longitude;

    private Double latitude;

    @Column(name = "order_date")
    private LocalDateTime orderDate = Utils.getDateTimeNow();

    @Column(name = "accept_date")
    private LocalDateTime acceptDate;

    @Column(name = "rj_reason_description")
    private String rjReasonDescription;

    @Column(name = "request_count")
    private Integer requestCount;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "total_price_actual")
    private Double totalPriceActual;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "renter_id")
    private User renter;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "manager_id")
    private User manager;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "rejection_reason_id")
    private RejectionReason rejectionReason;

    @OneToMany(mappedBy = "booking")
    @JsonIgnoreProperties("booking")
    @JsonIgnore
    private List<BookingDetail> bookingDetails;

    @OneToMany(mappedBy = "booking")
    @JsonIgnoreProperties("booking")
    @JsonIgnore
    private List<BookingStatusHistory> bookingStatusHistories;

    @OneToOne(mappedBy = "booking")
    @JsonIgnoreProperties("booking")
    private Report report;

}