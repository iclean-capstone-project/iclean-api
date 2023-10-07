package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
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
    private LocalDateTime orderDate;

    @Column(name = "accept_date")
    private LocalDateTime acceptDate;

    @Column(name = "rj_reason_description")
    private String rjReasonDescription;

    @Column(name = "work_date")
    private LocalDateTime workDate;

    @Column(name = "work_hour")
    private Double workHour;

    @Column(name = "work_hour_actual")
    private Double workHourActual;

    @Column(name = "request_count")
    private Integer requestCount;

    @Column(name = "work_start")
    private LocalDateTime workStart;

    @Column(name = "work_end")
    private LocalDateTime workEnd;

    private Double rate;

    private String feedback;

    @Column(name = "feedback_time")
    private LocalDateTime feedbackTime;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "renter_id", insertable = true, updatable = true)
    private User renter;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "staff_id", insertable = true, updatable = true)
    private User staff;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "rj_reason_id", insertable = false, updatable = false)
    private RejectReason rejectReason;

    @OneToMany(mappedBy = "booking")
    @JsonIgnoreProperties("booking")
    @JsonIgnore
    private List<ImgBooking> imgBookingList;

    @OneToMany(mappedBy = "booking")
    @JsonIgnoreProperties("booking")
    @JsonIgnore
    private List<BookingStatusHistory> bookingStatusHistories;

    @OneToOne(mappedBy = "booking")
    @JsonIgnoreProperties("booking")
    private Report report;

}