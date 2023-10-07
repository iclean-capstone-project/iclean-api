package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class BookingStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_history_id")
    private Integer statusHistoryId;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "booking_id", insertable = false, updatable = false)
    private Integer bookingId;

    @Column(name = "booking_status_id", insertable = false, updatable = false)
    private Integer bookingStatusId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "booking_status_id")
    private BookingStatus bookingStatus;
}
