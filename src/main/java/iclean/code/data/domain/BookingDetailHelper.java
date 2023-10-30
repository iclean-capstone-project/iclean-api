package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iclean.code.data.enumjava.BookingDetailHelperStatusEnum;
import iclean.code.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class BookingDetailHelper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_detail_helper_id")
    private Integer bookingDetailHelperId;

    @Column(name = "booking_detail_helper_status")
    private BookingDetailHelperStatusEnum bookingDetailHelperStatus;

    @Column(name = "create_at")
    private LocalDateTime createAt = Utils.getDateTimeNow();

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "booking_detail_id")
    private BookingDetail bookingDetail;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "service_registration_id")
    private ServiceRegistration serviceRegistration;
}
