package iclean.gfa23se46.icleanapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
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
