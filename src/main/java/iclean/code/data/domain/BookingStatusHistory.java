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

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "booking_id", insertable = true, updatable = true)
    private Booking booking;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "booking_status_id", insertable = true, updatable = true)
    private BookingStatus bookingStatus;
}
