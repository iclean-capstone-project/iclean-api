package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iclean.code.data.enumjava.BookingDetailStatusEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class BookingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_detail_id")
    private Integer bookingDetailId;

    @Column(name = "price_detail")
    private Double priceDetail;

    @Column(name = "booking_detail_status")
    private BookingDetailStatusEnum bookingDetailStatusEnum;

    @Column(name = "work_hour")
    private Double workHour;

    @Column(name = "work_date")
    private LocalDateTime workDate;

    @Column(name = "work_start")
    private LocalDateTime workStart;

    @Column(name = "work_end")
    private LocalDateTime workEnd;

    @Column(name = "work_hour_actual")
    private Double workHourActual;

    private Double rate;

    private String feedback;

    @Column(name = "feedback_time")
    private LocalDateTime feedbackTime;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @OneToMany(mappedBy = "bookingDetail")
    @JsonIgnoreProperties("bookingDetail")
    @JsonIgnore
    private List<BookingDetailHelper> bookingDetailHelpers;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "service_unit_id")
    private ServiceUnit serviceUnit;
}
