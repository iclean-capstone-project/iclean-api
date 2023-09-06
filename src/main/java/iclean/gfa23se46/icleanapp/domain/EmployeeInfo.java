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
public class EmployeeInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "is_online", nullable = false)
    private int isOnline;

    @Column(name = "sum_hour", nullable = false)
    private double sumHour;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "contract_id", insertable = false, updatable = false)
    private Contract contract;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    private TypeStaff typeStaff;

    @OneToMany(mappedBy = "employeeInfo")
    @JsonIgnoreProperties("employeeInfo")
    @JsonIgnore
    private List<CalendarStaff> calendarStaffs;

    @OneToMany(mappedBy = "renter")
    @JsonIgnoreProperties("employeeInfo")
    @JsonIgnore
    private List<Booking> renterBookings;

    @OneToMany(mappedBy = "staff")
    @JsonIgnoreProperties("employeeInfo")
    @JsonIgnore
    private List<Booking> staffBookings;
}
