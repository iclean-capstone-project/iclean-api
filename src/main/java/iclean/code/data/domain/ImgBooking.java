package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class ImgBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_booking_id")
    private Integer imgBookingId;

    @Column(name = "img_booking_link")
    private String imgBookingLink;

    @Column(name = "create_at")
    private Date createAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "booking_id", insertable = false, updatable = false)
    private Booking booking;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "img_type_id", insertable = false, updatable = false)
    private ImgType imgType;
}
