package iclean.gfa23se46.icleanapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class ImgBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_booking_id")
    private int imgBookingId;

    private String img;

    @Column(name = "create_at")
    private Date createAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "booking_id", insertable = false, updatable = false)
    private Booking booking;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "type_id", insertable = false, updatable = false)
    private TypeImgBooking typeImgBooking;
}
