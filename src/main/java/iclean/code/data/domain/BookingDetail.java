package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iclean.code.data.enumjava.BookingDetailStatusEnum;
import iclean.code.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Column(name = "work_date")
    private LocalDate workDate;

    @Column(name = "work_start")
    private LocalTime workStart;

    @Column(name = "work_end")
    private LocalTime workEnd;

    @Column(name = "work_hour_actual")
    private Double workHourActual;

    private Double rate;

    private String feedback;

    @Column(name = "feedback_time")
    private LocalDateTime feedbackTime;

    @Column(name = "work_end")
    private LocalDateTime createAt = Utils.getDateTimeNow();

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
