package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class BookingStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_status_id")
    private int bookingStatusId;

    @OneToMany(mappedBy = "bookingStatus")
    @JsonIgnoreProperties("bookingStatus")
    @JsonIgnore
    private List<Booking> bookings;

    @Column(name = "title_status")
    private String titleStatus;
}
