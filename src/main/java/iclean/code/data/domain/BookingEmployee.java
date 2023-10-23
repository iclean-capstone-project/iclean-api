package iclean.code.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class BookingEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_emp_id")
    private Integer bookingEmpId;

    @Column(name = "is_accept")
    private boolean isAccept = false;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "booking_id", insertable = true, updatable = true)
    private Booking booking;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "employee_id", insertable = true, updatable = true)
    private User employee;
}