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
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private int bookingId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "renter_id", insertable = false, updatable = false)
    private EmployeeInfo renter;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "staff_id", insertable = false, updatable = false)
    private EmployeeInfo staff;

    private String location;

    @Column(name = "location_description")
    private String locationDescription;

    private double longitude;

    private double latitude;

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "work_date")
    private Date workDate;

    @Column(name = "work_time")
    private double workTime;

    @Column(name = "work_time_actual")
    private double workTimeActual;

    @Column(name = "work_start")
    private Date workStart;

    @Column(name = "work_end")
    private Date workEnd;

    private double rate;

    private String feedback;

    @Column(name = "feedback_time")
    private Date feedbackTime;

    @Column(name = "total_price")
    private double totalPrice;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties("booking")
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @OneToMany(mappedBy = "booking")
    @JsonIgnoreProperties("booking")
    @JsonIgnore
    private List<ImgBooking> imgBookings;

    @OneToMany(mappedBy = "booking")
    @JsonIgnoreProperties("booking")
    @JsonIgnore
    private List<VoucherUser> voucherUsers;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "booking_status_id")
    private BookingStatus bookingStatus;

}
