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
    private int bookingId;

    private String location;

    @Column(name = "location_description")
    private String locationDescription;

    private double longitude;

    private double latitude;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "accept_date")
    private LocalDateTime acceptDate;

    @Column(name = "rj_reason_description")
    private String rjReasonDescription;

    @Column(name = "work_date")
    private LocalDateTime workDate;

    @Column(name = "work_hour")
    private double workHour;

    @Column(name = "work_hour_actual")
    private double workHourActual;

    @Column(name = "request_count")
    private double requestCount;

    @Column(name = "work_start")
    private LocalDateTime workStart;

    @Column(name = "work_end")
    private LocalDateTime workEnd;

    private double rate;

    private String feedback;

    @Column(name = "feedback_time")
    private LocalDateTime feedbackTime;

    @Column(name = "total_price")
    private double totalPrice;

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
    @JoinColumn(name = "job_id", insertable = true, updatable = true)
    private Job job;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "booking_status_id", insertable = true, updatable = true)
    private BookingStatus bookingStatus;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "rj_reason_id", insertable = false, updatable = false)
    private RejectReasion rejectReasion;

    @OneToMany(mappedBy = "booking")
    @JsonIgnoreProperties("booking")
    @JsonIgnore
    private List<ImgBooking> imgBookingList;


    @OneToOne(mappedBy = "booking")
    @JsonIgnoreProperties("booking")
    private Report report;

}