package iclean.code.data.repository;

import iclean.code.data.domain.BookingEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingEmployeeRepository extends JpaRepository<BookingEmployee, Integer> {

//    @Query("SELECT bookingEmpl " +
//            "FROM BookingEmployee bookingEmpl " +
//            "WHERE bookingEmpl.employee.userId = ?1 limit ?")
    Optional<BookingEmployee> findTopByEmployeeUserIdOrderByBookingEmpIdDesc (Integer empId);
}
